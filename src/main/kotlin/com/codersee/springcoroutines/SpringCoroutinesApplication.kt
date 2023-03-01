package com.codersee.springcoroutines

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringCoroutinesApplication

fun main(args: Array<String>) {
	runApplication<SpringCoroutinesApplication>(*args)
}
