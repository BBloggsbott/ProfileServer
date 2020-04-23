package org.bbloggsbott.profile.application.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class PropertiesDTO(
        @JsonProperty("email") val email: String,
        @JsonProperty("base_directory") var baseDir: String,
        @JsonProperty("data_directory") var dataDirectory: String,
        @JsonProperty("navigation_file") var navigationFile: String,
        @JsonProperty("profile_file") var profileFile: String,
        @JsonProperty("profile_image_url") var imageUrl: String?,
        @JsonProperty("profile_image_size") var imageSize: String?
)