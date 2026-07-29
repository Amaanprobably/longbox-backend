package com.example.routes

import com.example.cache.AllCacheStatsDto
import com.example.cache.CacheStatsDto
import com.example.cache.CharacterCache
import com.example.cache.CharacterListCache
import com.example.data.model.User
import com.example.data.requests.AuthRequest
import com.example.data.requests.RefreshRequest
import com.example.data.responses.AuthResponse
import com.example.data.user.UserDataSource
import com.example.domain.repository.ComicVineRepository
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.security.token.generateRefreshToken
import com.example.security.token.hashRefreshToken
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.signup(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
  post("auth/signup"){
     val request = call.receiveNullable<AuthRequest>() ?: run {
         call.respond(HttpStatusCode.BadRequest)
         return@post
     }
      val username = request.username.trim()
      val password = request.password
      if(!validUsername(username) || !validPassword(password)){
          call.respond(HttpStatusCode.UnprocessableEntity)
          return@post
      }
      val saltedHash = hashingService.generateSaltedHash(password)
      val user = User(
          username = username,
          password = saltedHash.hash,
          salt = saltedHash.salt
      )
      val wasAcknowledged = userDataSource.insertUser(user)
      if (!wasAcknowledged) {
          call.respond(HttpStatusCode.Conflict)
          return@post
      }
      call.respond(HttpStatusCode.OK)
  }
}

fun Route.signin(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("auth/signin"){
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val username = request.username.trim()
        val password = request.password
        if(!validUsername(username) || !validPassword(password)){
            call.respond(HttpStatusCode.UnprocessableEntity)
            return@post
        }
        val user = userDataSource.getUserByUsername(username)
        if(user == null){
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        val isCorrectPassword = hashingService.verify(
                password = password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
        )
        if(!isCorrectPassword){
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        val authToken = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                "userId",
                user.id.toString()
            )
        )
        val refreshToken = generateRefreshToken()
        userDataSource.saveRefreshToken(
            userId = user.id,
            refreshToken = hashRefreshToken(refreshToken),
            refreshTokenExpiresAt = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000) //14 days
        )
        call.respond(HttpStatusCode.OK, AuthResponse(authToken, refreshToken))
    }
}

fun Route.authenticateRoute(){
    get("auth/authenticate"){
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        call.respond(HttpStatusCode.OK, mapOf("userId" to userId))
    }
}

fun Route.refresh(userDataSource: UserDataSource, tokenService: TokenService, tokenConfig: TokenConfig){
    post("auth/refresh"){
        val request = call.receiveNullable<RefreshRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (request.refreshToken.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUserByRefreshToken(request.refreshToken)
            ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val expiresAt = user.refreshTokenExpiresAt ?: 0L
        if (System.currentTimeMillis() > expiresAt) {
            // clear expired token from DB
            userDataSource.revokeRefreshToken(user.id)
            return@post call.respond(HttpStatusCode.Unauthorized)
        }
        // generate new tokens (rotation)
        val newAccessToken = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                "userId",
                user.id.toString()
            )
        )
        val newRefreshToken = generateRefreshToken()
        userDataSource.saveRefreshToken(
            user.id,
            hashRefreshToken(newRefreshToken),
            System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000) //14 days
        )
        call.respond(AuthResponse(newAccessToken, newRefreshToken))
    }
}
fun Route.getCharacter(comicVineRepository: ComicVineRepository) {
    get("/characters/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid character id")
            return@get
        }
        val character = comicVineRepository.getCharacter(id)
        call.respond(HttpStatusCode.OK, character)
    }
}

fun Route.searchCharacters(comicVineRepository: ComicVineRepository) {
    get("/characters/search") {
        val query = call.request.queryParameters["query"]?.trim()
        val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
        if (query.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Query parameter is required")
            return@get
        }
        val results = comicVineRepository.searchCharacters(query,offset)
        call.respond(HttpStatusCode.OK, results)
    }
}
fun Route.getBrowseCharacters(comicVineRepository: ComicVineRepository) {
    get("/characters") {
        val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
        val results = comicVineRepository.getBrowseCharacters(offset)
        call.respond(HttpStatusCode.OK, results)
    }
}
fun Route.getCacheStats(characterCache: CharacterCache,characterListCache: CharacterListCache){
    get("/admin/cache-stats") {
        val charStats = characterCache.stats()
        val listStats = characterListCache.stats()

        call.respond(
            AllCacheStatsDto(
                characterCache = CacheStatsDto(
                    hitRate = charStats.hitRate(),
                    missRate = charStats.missRate(),
                    hitCount = charStats.hitCount(),
                    missCount = charStats.missCount(),
                    evictionCount = charStats.evictionCount(),
                    estimatedSize = characterCache.estimatedSize()
                ),
                listCache = CacheStatsDto(
                    hitRate = listStats.hitRate(),
                    missRate = listStats.missRate(),
                    hitCount = listStats.hitCount(),
                    missCount = listStats.missCount(),
                    evictionCount = listStats.evictionCount(),
                    estimatedSize = characterListCache.estimatedSize()
                )
            )
        )
    }
}

fun validUsername(username:String) = username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))
fun validPassword(password:String) = password.isNotBlank() && password.length >= 8 &&
        password.any { it.isUpperCase() } && password.any { it.isLowerCase() } &&
        password.any { !it.isLetterOrDigit() } && password.any { it.isDigit() }