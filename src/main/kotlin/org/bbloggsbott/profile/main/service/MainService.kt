package org.bbloggsbott.profile.main.service

import org.bbloggsbott.profile.main.dto.MainResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MainService {

    @Autowired
    private lateinit var profileService: ProfileService

    @Autowired
    private lateinit var navigationService: NavigationService

    fun getMainResponse(): MainResponseDTO{
        return MainResponseDTO(
                profileService.getProfile(),
                navigationService.getNavigationDTO()
        )
    }

}