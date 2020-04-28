package org.bbloggsbott.profile.media.exception

import java.io.FileNotFoundException

class ResourceNotFoundException(message: String, name: String) : FileNotFoundException(message){
    val resourceName = name
}