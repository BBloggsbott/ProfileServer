package org.bbloggsbott.profile.pages.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.pages.dto.PageDTO
import org.bbloggsbott.profile.pages.dto.PagePathDTO
import org.bbloggsbott.profile.pages.dto.PageResponseDTO
import org.bbloggsbott.profile.pages.exception.PageNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.*
import javax.annotation.PostConstruct

@Service
class PagePathService {

    @Autowired
    lateinit var propertyService: PropertyService
    @Autowired
    lateinit var markdownService: MarkdownService

    lateinit var pagePaths: PagePathDTO

    lateinit var directoryPaths: HashMap<String, ArrayList<String>>

    lateinit var basePaths: HashSet<String>

    val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun loadPagePaths(){
        logger.info("Creating custom paths from pages")
        pagePaths = PagePathDTO(HashMap<String, String>())
        directoryPaths = HashMap<String, ArrayList<String>>()
        basePaths = HashSet<String>()
        val pagesDirectory = propertyService.getProperties().absolutePagePath
        createDirectoryWatching(pagesDirectory!!)
        var directories = setPathsInDirectoryAndGetChildDirectories(File(pagesDirectory))
        createWatchersForDirectories(directories)
        do {
            directories = directories.map { setPathsInDirectoryAndGetChildDirectories(it) }.flatten()
            createWatchersForDirectories(directories)
        } while (directories.size != 0)
        logger.info("Found ${pagePaths.pathMap.size} pages and ${directoryPaths.size} intermediate paths")
    }

    fun createWatchersForDirectories(directories: List<File>){
        for (directory in directories){
            createDirectoryWatching(directory.absolutePath)
        }
    }

    fun setPathsInDirectoryAndGetChildDirectories(directory: File): List<File>{
        val files = directory.listFiles()
        val directories = ArrayList<File>()
        for (file in files){
            if (file.isFile && file.extension=="md"){
                var pageDTO = PageDTO(file.absolutePath)
                pageDTO.contentMD = File(pageDTO.pagePath).readText()
                markdownService.setPageDTO(pageDTO)
                pagePaths.pathMap[pageDTO.permalink!!.trim('/')] = pageDTO.pagePath
                makePageDirectoryEntry(pageDTO)
            } else if (file.isDirectory){
                directories.add(file)
            }
        }
        return directories
    }

    fun makePageDirectoryEntry(pageDTO: PageDTO){
        logger.info("Creating entries for child paths")
        val paths = pageDTO.permalink!!.trim('/').split("/")
        var relativePaths = ArrayList<String>()

        basePaths.add(paths[0])
        relativePaths.add(paths[0])
        var idx = 1

        while (idx < paths.size){
            var path = relativePaths[idx-1] + "/" + paths[idx]
            relativePaths.add(path)
            if ((relativePaths[idx-1] in directoryPaths.keys).not()){
                directoryPaths[relativePaths[idx-1]] = ArrayList<String>()
            }
            directoryPaths[relativePaths[idx-1]]!!.add(path)
            idx+=1
        }
    }

    fun createPageDTO(pagePermalink: String): PageDTO{
        val pageFile = File(pagePaths.pathMap[pagePermalink])
        var pageDTO = PageDTO(pageFile.absolutePath)
        pageDTO.contentMD = pageFile.readText()
        markdownService.setPageDTO(pageDTO)
        return pageDTO
    }

    fun getPageResponse(pageUrl: String): PageResponseDTO{
        logger.info("Got a request to url $pageUrl")
        var pageResponse: PageResponseDTO? = null
        val cleanedUrl = pageUrl.trim('/')
        if (cleanedUrl in directoryPaths.keys){
            logger.info("Found a directory entry")
            pageResponse = PageResponseDTO()
            pageResponse.isPage = false
            pageResponse.children.addAll(directoryPaths[cleanedUrl]!!)
        } else if (cleanedUrl in pagePaths.pathMap.keys) {
            logger.info("Found a page entry")
            pageResponse = PageResponseDTO()
            pageResponse.page = createPageDTO(cleanedUrl)
        } else {
            throw PageNotFoundException("Page Not found", pageUrl)
        }
        return pageResponse
    }

    fun createDirectoryWatching(directoryName: String){
        logger.info("Creating watch service for $directoryName")
        val watchService: WatchService = FileSystems.getDefault().newWatchService()
        val path: Path = Paths.get(directoryName)
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
        )
        GlobalScope.launch(newSingleThreadContext("$directoryName Watcher")){
            var watchKey: WatchKey? = null
            logger.info("Starting directory monitoring for $directoryName")
            watchKey = watchService.take()
            while (watchKey != null){
                for (event in watchKey.pollEvents()){
                    logger.info("Event: ${event.kind()} File: ${event.context()}")
                }
                watchKey.reset()
                watchKey = watchService.take()
            }
        }
    }

}