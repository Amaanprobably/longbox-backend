package com.example.plugins

import com.example.cache.CharacterCache
import com.example.cache.CharacterListCache
import com.example.routes.authenticateRoute
import com.example.data.user.UserDataSource
import com.example.domain.repository.ComicVineRepository
import com.example.routes.getBrowseCharacters
import com.example.routes.getCacheStats
import com.example.routes.getCharacter
import com.example.routes.refresh
import com.example.routes.searchCharacters
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.routes.signin
import com.example.routes.signup
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userDataSource by inject<UserDataSource>()
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()
    val tokenConfig by inject<TokenConfig>()
    val comicVineRepository by inject<ComicVineRepository>()
    val characterCache by inject<CharacterCache>(named("characterCache"))
    val characterListCache by inject<CharacterListCache>(named("characterListCache"))
    routing {
        rateLimit(RateLimitName("auth")) {
            signup(hashingService, userDataSource)
            signin(hashingService, userDataSource, tokenService, tokenConfig)
            refresh(userDataSource, tokenService, tokenConfig)
        }
        rateLimit(RateLimitName("admin")) {
            authenticate("admin-auth") {
                getCacheStats(characterCache, characterListCache)
            }
        }
        authenticate{
            getCharacter(comicVineRepository)
            searchCharacters(comicVineRepository)
            getBrowseCharacters(comicVineRepository)
            authenticateRoute()
        }
    }
}