package org.bbloggsbott.profile.main.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SocialMediaDTO(
        @JsonProperty("twitter") var twitter: String,
        @JsonProperty("linkedin") var linkedin: String,
        @JsonProperty("github") var github: String,
        @JsonProperty("gitlab") var gitlab: String
)