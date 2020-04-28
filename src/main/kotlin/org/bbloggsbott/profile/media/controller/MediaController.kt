package org.bbloggsbott.profile.media.controller

import org.bbloggsbott.profile.media.exception.ResourceNotFoundException
import org.bbloggsbott.profile.media.service.MediaService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MediaController {

    @Autowired
    private lateinit var mediaService: MediaService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/files/{filename}")
    fun getFile(@PathVariable("filename") filename: String): ResponseEntity<ByteArray>{
        logger.info("Got request for file $filename")
        val content = mediaService.getFileAsByteArray(filename)
        var headers = HttpHeaders()
        headers.contentType = mediaService.getMediaTypeFromFileName(filename)
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        var response = ResponseEntity(content, headers, HttpStatus.OK)
        return response
    }

}