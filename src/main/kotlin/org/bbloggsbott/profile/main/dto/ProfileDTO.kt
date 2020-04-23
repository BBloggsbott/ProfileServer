package org.bbloggsbott.profile.main.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ProfileDTO (
        @JsonProperty("name") val name: String,
        @JsonProperty("avatar") var avatar: String,
        @JsonProperty("bio") val bio: String,
        @JsonProperty("location") val location: String,
        @JsonProperty("email") val email: String,
        @JsonProperty("social_media") var socialMedia: SocialMediaDTO
)