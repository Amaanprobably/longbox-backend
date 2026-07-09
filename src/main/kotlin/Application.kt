package com.example

import com.example.data.user.MongoUserDataSource
import com.example.plugins.configureDefaultHeader
import com.example.plugins.configureHttp
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRateLimiting
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module(){
    val connectionString = environment.config.property("ktor.database.connectionString").getString()
    val dbName = environment.config.property("ktor.database.dbName").getString()
    val db = MongoClient.create(
        connectionString = connectionString
    ).getDatabase(dbName)
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 1000L * 60L * 60L, // 1 hour
        secret = environment.config.property("jwt.secret").getString(),
    )
    val hashingService = SHA256HashingService()
    configureSecurity()
    configureSerialization()
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
    configureRateLimiting()
    configureStatusPages()
    configureMonitoring()
    configureKoin()
    configureHttp()
    configureDefaultHeader()
}