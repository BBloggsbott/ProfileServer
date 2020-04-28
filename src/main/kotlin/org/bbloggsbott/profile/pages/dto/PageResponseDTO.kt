package org.bbloggsbott.profile.pages.dto

import com.fasterxml.jackson.annotation.JsonProperty

class PageResponseDTO{
    @JsonProperty("is_page") var isPage: Boolean = true
    @JsonProperty("children") var children = ArrayList<String>()
    @JsonProperty("page") var page: PageDTO? = null
}