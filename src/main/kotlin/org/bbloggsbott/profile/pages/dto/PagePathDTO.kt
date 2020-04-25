package org.bbloggsbott.profile.pages.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PagePathDTO(
        var pathMap: HashMap<String, String>
)