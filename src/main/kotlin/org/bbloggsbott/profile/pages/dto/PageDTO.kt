package org.bbloggsbott.profile.pages.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PageDTO(@JsonIgnore val pagePath: String){
    @JsonProperty("title") var  title: String? = null
    @JsonProperty("permalink") var permalink: String? = null
    @JsonProperty("date") var date: Date? = null
    @JsonProperty("paper_url") var paperUrl: String? = null
    @JsonProperty("content_md") var contentMD: String? = null
    @JsonProperty("content_html") var contentHTML: String? = null
    @JsonProperty("collections") var pageCollections: List<String>? = null
    @JsonProperty("excerpt") var excerpt: String? = null
    @JsonProperty("page_venue") var pageVenue: String? = null
}