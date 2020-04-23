package org.bbloggsbott.profile.main.controller

import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.main.dto.ProfileDTO
import org.bbloggsbott.profile.main.service.NavigationService
import org.bbloggsbott.profile.main.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

    @Autowired
    lateinit var propertyService: PropertyService
    @Autowired
    lateinit var navigationService: NavigationService
    @Autowired
    lateinit var profileService: ProfileService

    @GetMapping("/sample")
    fun sampleController(): ProfileDTO{
        return profileService.getProfile()
    }
}
