package org.bbloggsbott.profile.main.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.main.dto.NavigationDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct

@Service
class NavigationService {

    @Autowired
    private lateinit var propertyService: PropertyService

    private lateinit var navigationDTO: NavigationDTO

    @PostConstruct
    fun loadNavigation(){
        val mapper: ObjectMapper = ObjectMapper(YAMLFactory())
        navigationDTO = mapper.readValue(File(propertyService.getProperties()!!.navigationFile), NavigationDTO::class.java)
    }

    fun getNavigationDTO(): NavigationDTO{
        return navigationDTO
    }
}