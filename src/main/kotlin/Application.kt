package com.example

import com.example.data.cache.CharacterCache
import com.example.data.cache.CharacterListCache
import com.example.plugins.configureHttp
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRateLimiting
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.plugins.startCacheStatsLogging
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module(){
    configureKoin()
    configureSecurity()
    configureRateLimiting()
    configureSerialization()
    configureRouting()
    configureStatusPages()
    configureMonitoring()
    configureHttp()
    //Cache Logging
    val characterCache by inject<CharacterCache>(named("characterCache"))
    val characterListCache by inject<CharacterListCache>(named("characterListCache"))
    startCacheStatsLogging(characterCache, characterListCache)
}