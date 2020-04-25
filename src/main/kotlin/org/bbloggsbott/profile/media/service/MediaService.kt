package org.bbloggsbott.profile.media.service

import org.bbloggsbott.profile.application.dto.PropertiesDTO
import org.bbloggsbott.profile.application.service.PropertyService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Service
class MediaService {

    @Autowired
    private lateinit var propertyService: PropertyService

    private lateinit var propertiesDTO: PropertiesDTO

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun getFileAsByteArray(filename: String): ByteArray{
        val filepath = Paths.get(propertiesDTO.fileDirectory, filename).toString()
        logger.info("Getting file from $filepath")
        val file = File(filepath)
        if (!file.exists()){
            throw FileNotFoundException("Requested media file not found")
        }
        return file.readBytes()
    }

    fun getMediaTypeFromFileName(filename: String): MediaType?{
        val extension = filename.split(".")[1]
        when(extension){
            "pdf" -> return MediaType.APPLICATION_PDF
            "json" -> return MediaType.APPLICATION_JSON
            "png" -> return MediaType.IMAGE_PNG
            "jpg" -> return MediaType.IMAGE_JPEG
            "jpeg" -> return MediaType.IMAGE_JPEG
            "gif" -> return MediaType.IMAGE_GIF
            "txt" -> return MediaType.TEXT_PLAIN
            "html" -> return MediaType.TEXT_HTML
            "md" -> return MediaType.TEXT_MARKDOWN
            "xml" -> return MediaType.TEXT_XML
            else -> return MediaType.ALL
        }
    }

    @PostConstruct
    fun initializeMembers(){
        propertiesDTO = propertyService.getProperties()
    }

}