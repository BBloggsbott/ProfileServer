package org.bbloggsbott.profile.extension
import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String{
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(trim().toByteArray())).toString(16).padStart(32, '0').toString()
}