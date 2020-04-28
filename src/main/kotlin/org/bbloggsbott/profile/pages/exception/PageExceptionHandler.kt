package org.bbloggsbott.profile.pages.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PageExceptionHandler {

    @ExceptionHandler(value = [(PageNotFoundException::class)])
    fun pageNotFoundExceptionHandler(ex: PageNotFoundException): ResponseEntity<String>{
        return ResponseEntity("${ex.pageUrl} not found", HttpStatus.NOT_FOUND)
    }

}