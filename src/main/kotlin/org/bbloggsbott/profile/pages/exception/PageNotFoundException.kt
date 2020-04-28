package org.bbloggsbott.profile.pages.exception

import java.lang.Exception

class PageNotFoundException(message: String, url:String): Exception(message) {
    val pageUrl = url
}