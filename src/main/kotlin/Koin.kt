package com.example

import com.example.data.cache.CharacterCache
import com.example.data.cache.CharacterListCache
import com.example.data.cache.createCharacterCache
import com.example.data.cache.createCharacterListCache
import com.example.data.comicvine.ComicVineClient
import com.example.data.comicvine.ComicVineRateLimiter
import com.example.data.repository.ComicVineRepositoryImpl
import com.example.data.user.MongoUserDataSource
import com.example.domain.repository.UserDataSource
import com.example.domain.repository.ComicVineRepository
import com.example.plugins.createHttpClient
import com.example.security.hashing.HashingService
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.client.HttpClient
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule(environment.config))
    }
}
fun appModule(config: ApplicationConfig) = module {
    single {
        MongoClient.create(
            connectionString = config.property("ktor.database.connectionString").getString()
        ).getDatabase(config.property("ktor.database.dbName").getString())
    }

    single<UserDataSource> { MongoUserDataSource(get()) }

    single<HashingService> { SHA256HashingService() }

    single<TokenService> { JwtTokenService() }

    single {
        TokenConfig(
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            expiresIn = 1000L * 60L * 60L,
            secret = config.property("jwt.secret").getString()
        )
    }

    single<HttpClient> { createHttpClient() }

    single<CharacterCache>(named("characterCache")) { createCharacterCache() }

    single<CharacterListCache>(named("characterListCache")) { createCharacterListCache() }

    single<ComicVineRateLimiter> { ComicVineRateLimiter() }

    single{
        ComicVineClient(
            httpClient = get(),
            rateLimiter = get()
        )
    }

    single<ComicVineRepository> {
        ComicVineRepositoryImpl(
            characterCache = get(named("characterCache")),
            listCache = get(named("characterListCache")),
            client = get(),
            apiKey = config.property("comicvine.api.key").getString()
        )
    }
}
