package org.bbloggsbott.profile.pages.service

import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.pages.dto.PageDTO
import org.bbloggsbott.profile.pages.dto.PagePathDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Service
class PagePathService {

    @Autowired
    lateinit var propertyService: PropertyService
    @Autowired
    lateinit var markdownService: MarkdownService

    lateinit var pagePaths: PagePathDTO

    lateinit var directoryPaths: PagePathDTO

    @PostConstruct
    fun loadPagePaths(){
        pagePaths = PagePathDTO(HashMap<String, String>())
        directoryPaths = PagePathDTO(HashMap<String, String>())
        val pagesDirectory = propertyService.getProperties().absolutePagePath
        var directories = setPathsInDirectoryAndGetChildDirectories(File(pagesDirectory))
        do {
            directories = directories.map { setPathsInDirectoryAndGetChildDirectories(it) }.flatten()
        } while (directories.size != 0)
    }

    fun setPathsInDirectoryAndGetChildDirectories(directory: File): List<File>{
        val files = directory.listFiles()
        val directories = ArrayList<File>()
        for (file in files){
            if (file.isFile && file.extension=="md"){
                var pageDTO = PageDTO(file.absolutePath)
                pageDTO.contentMD = File(pageDTO.pagePath).readText()
                markdownService.setPageDTO(pageDTO)
                pagePaths.pathMap[pageDTO.permalink!!] = pageDTO.pagePath
            } else if (file.isDirectory){
                directories.add(file)
            }
        }
        return directories
    }

}