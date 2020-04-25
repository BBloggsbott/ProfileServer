package org.bbloggsbott.profile.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.bbloggsbott.profile.application.dto.PropertiesDTO
import org.bbloggsbott.profile.extension.DEFAULT_DATETIME_FORMAT
import org.bbloggsbott.profile.extension.md5
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Service
class PropertyService {

    @Value("\${profile.settings.file}")
    private lateinit var settingsFilename: String

    @Value("\${gravatar.avatar.baseurl}")
    private lateinit var gravatarBaseUrl: String

    private lateinit var properties: PropertiesDTO

    @PostConstruct
    fun postConstruct(){
        val mapper: ObjectMapper = ObjectMapper()
        val settingsFile: File = File(settingsFilename)
        if (!settingsFile.exists()){
            throw FileNotFoundException("$settingsFilename not found")
        }
        properties = mapper.readValue(settingsFile, PropertiesDTO::class.java)
        createGravatarURL()
        processPaths()
        if (properties.dateTimeFormat == null){
            properties.dateTimeFormat = DEFAULT_DATETIME_FORMAT
        }
    }

    fun getProperties(): PropertiesDTO = properties

    private fun createGravatarURL(){
        val hashEmail: String = properties.email.md5()
        if (properties.imageSize != null){
            properties.imageUrl =  gravatarBaseUrl + hashEmail + "?s=${properties.imageSize}"
            return
        }
        properties.imageUrl =  gravatarBaseUrl + hashEmail
    }

    private fun processPaths(){
        if (properties.baseDir.equals(".")){
            properties.baseDir = System.getProperty("user.dir")
        }
        properties.dataDirectory = Paths.get(properties.baseDir, properties.dataDirectory).toString()
        properties.navigationFile = Paths.get(properties.dataDirectory, properties.navigationFile).toString()
        properties.profileFile = Paths.get(properties.dataDirectory, properties.profileFile).toString()
        properties.fileDirectory = Paths.get(properties.baseDir, properties.fileDirectory).toString()
        properties.absolutePagePath = Paths.get(properties.baseDir, properties.pagesDirectory).toString()
    }

}