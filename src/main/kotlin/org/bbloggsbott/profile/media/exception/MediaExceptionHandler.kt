package org.bbloggsbott.profile.media.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class MediaExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [(ResourceNotFoundException::class)])
    fun handleResourceNotFoundExceptionHandler(ex: ResourceNotFoundException): ResponseEntity<String>{
        return ResponseEntity("Resource ${ex.resourceName} not found", HttpStatus.NOT_FOUND)
    }

}