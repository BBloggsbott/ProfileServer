package org.bbloggsbott.profile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProfileApplication

fun main(args: Array<String>) {
	runApplication<ProfileApplication>(*args)
}
