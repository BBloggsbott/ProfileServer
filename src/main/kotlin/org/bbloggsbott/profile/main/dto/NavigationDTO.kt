package org.bbloggsbott.profile.main.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Getter
import lombok.Setter

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class NavigationDTO(
        @JsonProperty("paths") val navigation: List<NavigationItemDTO>
)