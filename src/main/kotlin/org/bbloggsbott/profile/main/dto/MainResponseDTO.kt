package org.bbloggsbott.profile.main.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MainResponseDTO(
        @JsonProperty("profile") val profile: ProfileDTO,
        @JsonProperty("navigation") val navigation: NavigationDTO
)