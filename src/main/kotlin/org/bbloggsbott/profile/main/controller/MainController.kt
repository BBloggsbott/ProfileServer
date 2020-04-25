package org.bbloggsbott.profile.main.controller

import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.main.dto.ProfileDTO
import org.bbloggsbott.profile.main.service.NavigationService
import org.bbloggsbott.profile.main.service.ProfileService
import org.bbloggsbott.profile.pages.dto.PageDTO
import org.bbloggsbott.profile.pages.dto.PagePathDTO
import org.bbloggsbott.profile.pages.service.MarkdownService
import org.bbloggsbott.profile.pages.service.PagePathService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class MainController {

    @Autowired
    lateinit var propertyService: PropertyService
    @Autowired
    lateinit var navigationService: NavigationService
    @Autowired
    lateinit var profileService: ProfileService
    @Autowired
    lateinit var markdownService: MarkdownService
    @Autowired
    lateinit var pagePathService: PagePathService

    @GetMapping("/sample")
    fun sampleController(): PagePathDTO{
        return pagePathService.pagePaths
    }
}
