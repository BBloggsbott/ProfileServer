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

    lateinit var directoryPaths: HashMap<String, HashSet<String>>

    lateinit var basePaths: HashSet<String>

    val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun loadPagePaths(){
        logger.info("Creating custom paths from pages")
        pagePaths = PagePathDTO(HashMap<String, String>())
        directoryPaths = HashMap<String, HashSet<String>>()
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
                directoryPaths[relativePaths[idx-1]] = HashSet<String>()
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
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                        val createdFile = Paths.get(directoryName, event.context().toString()).toString()
                        logger.info("Create event detected $createdFile")
                        createPathsOnFileCreate(createdFile)
                        logger.info("Create event processed")
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        val modifiedFile = Paths.get(directoryName, event.context().toString()).toString()
                        logger.info("Modify file event detected in $modifiedFile")
                        modifyPathsOnFileModify(modifiedFile)
                        logger.info("Modify event processed")
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        val deletedFile = Paths.get(directoryName, event.context().toString()).toString()
                        logger.info("Delete file event detected in $deletedFile")
                        deletePathsOnFileDelete(deletedFile)
                        logger.info("Delete event processed")
                    }
                }
                watchKey.reset()
                watchKey = watchService.take()
            }
        }
    }

    fun createPathsOnFileCreate(filename: String){
        val file = File(filename)
        var directories: List<File>
        if (file.isDirectory){
            directories = setPathsInDirectoryAndGetChildDirectories(file)
            createWatchersForDirectories(directories)
            createDirectoryWatching(file.absolutePath)
            while (directories.size != 0){
                directories = directories.map { setPathsInDirectoryAndGetChildDirectories(it) }.flatten()
                createWatchersForDirectories(directories)
            }
        } else if (file.isFile && file.extension == "md"){
            var pageDTO = PageDTO(file.absolutePath)
            pageDTO.contentMD = file.readText()
            markdownService.setPageDTO(pageDTO)
            pagePaths.pathMap[pageDTO.permalink!!.trim('/')] = pageDTO.pagePath
            makePageDirectoryEntry(pageDTO)
        }
    }

    fun deletePathBasedOnFileName(filename: String){
        logger.info("${pagePaths.pathMap}")
        for (key in pagePaths.pathMap.filter { it.value == filename }.keys){
            pagePaths.pathMap.remove(key)
            deleteDirectoryPaths(key)
        }
    }

    fun modifyPathsOnFileModify(filename: String){
        val file = File(filename)
        if (file.isDirectory){
            return
        } else if (file.isFile && file.extension == "md") {
            var pageDTO = PageDTO(file.absolutePath)
            pageDTO.contentMD = file.readText()
            deletePathBasedOnFileName(file.absolutePath)
            markdownService.setPageDTO(pageDTO)
            pagePaths.pathMap[pageDTO.permalink!!.trim('/')] = pageDTO.pagePath
            makePageDirectoryEntry(pageDTO)
        }
    }

    fun deletePathsOnFileDelete(filename: String){
        val file = File(filename)
        if (deletePathForPagesInDirectory(filename)) return
        deletePathBasedOnFileName(filename)
    }

    fun deleteDirectoryPaths(permalink: String){
        if (permalink == "" || permalink == "/") return
        val lastPath = permalink.trim('/').split("/").last()
        val preambleUrl = permalink.trim('/').substring(0, permalink.length-lastPath.length).trim('/')
        if (preambleUrl != null && preambleUrl != ""){
            directoryPaths[preambleUrl] = HashSet(directoryPaths[preambleUrl]!!.filter { it != permalink })
        }
        if (directoryPaths[preambleUrl] == null || directoryPaths[preambleUrl]!!.size == 0){
            directoryPaths.remove(lastPath)
            deleteDirectoryPaths(preambleUrl)
            if (preambleUrl in basePaths){
                basePaths.remove(preambleUrl)
            }
        }
    }

    fun deletePathForPagesInDirectory(dirPath: String): Boolean{
        logger.info("Deleting all pages in directory $dirPath")
        var deleted = false
        var permalinks = pagePaths.pathMap.filter { it.value.contains(dirPath+"/") }.keys
        logger.info("Permalinks to delete $permalinks")
        for (link in permalinks){
            deleted = true
            pagePaths.pathMap.remove(link)
            deleteDirectoryPaths(link)
        }
        return deleted
    }

}