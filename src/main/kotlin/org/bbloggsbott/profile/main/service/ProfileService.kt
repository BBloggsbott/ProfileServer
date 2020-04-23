package org.bbloggsbott.profile.main.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.bbloggsbott.profile.application.dto.PropertiesDTO
import org.bbloggsbott.profile.application.service.PropertyService
import org.bbloggsbott.profile.main.dto.ProfileDTO
import org.bbloggsbott.profile.main.util.ProfileUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import javax.annotation.PostConstruct

@Service
class ProfileService {

    @Autowired
    private lateinit var propertyService: PropertyService
    @Autowired
    private lateinit var profileUtil: ProfileUtil

    val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var profileDTO: ProfileDTO

    @PostConstruct
    fun prepareProfileDTO(){
        logger.info("Reading Profile Information")
        val mapper: ObjectMapper = ObjectMapper(YAMLFactory())
        val profileFilename = propertyService.getProperties().profileFile
        val profileFile: File = File(profileFilename)
        if (!profileFile.exists()){
            logger.error("$profileFilename not found")
            throw FileNotFoundException("$profileFilename not found")
        }
        profileDTO = mapper.readValue(profileFile, ProfileDTO::class.java)
        logger.info("Starting profile preparation")
        if (profileDTO.avatar == null || profileDTO.avatar == ""){
            logger.info("User provided avatar not found. Using Gravatar instead")
            profileDTO.avatar = propertyService.getProperties()!!.imageUrl.toString()   // Use Gravatar if no avatar is provided
        }
        logger.info("Coverting social media usernames to profile URLs")
        profileUtil.prepareSocialMediaUrls(profileDTO)  // Convert usernames to profile urls
        logger.info("Profile preparation complete")
    }

    fun getProfile(): ProfileDTO = profileDTO
}