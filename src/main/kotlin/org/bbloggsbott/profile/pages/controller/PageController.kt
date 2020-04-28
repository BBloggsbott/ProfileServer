package org.bbloggsbott.profile.pages.controller

import org.bbloggsbott.profile.pages.dto.PageResponseDTO
import org.bbloggsbott.profile.pages.service.PagePathService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class PageController {

    @Autowired
    lateinit var pagePathService: PagePathService

    @GetMapping("/**")
    fun getPage(request: HttpServletRequest): PageResponseDTO{
        return pagePathService.getPageResponse(request.requestURI)
    }

}