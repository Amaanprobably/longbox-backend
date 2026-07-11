package com.example

import com.example.plugins.configureHttp
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRateLimiting
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module(){
    // val url = "https://comicvine.gamespot.com/api/search/?api_key=${apiKey}&format=json&resources=volume&query=Ultimate%20Spider-Man"
    configureKoin()
    configureSecurity()
    configureRateLimiting()
    configureSerialization()
    configureRouting()
    configureStatusPages()
    configureMonitoring()
    configureHttp()
}