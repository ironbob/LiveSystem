package com.wtb.livesystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.file.Paths

@SpringBootApplication
class LiveSystemApplication

fun main(args: Array<String>) {
    runApplication<LiveSystemApplication>(*args)
    println("Current working directory: ${Paths.get("").toAbsolutePath()}")
}
