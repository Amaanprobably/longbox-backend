package com.example.data.repository

import com.example.data.cache.CharacterCache
import com.example.data.cache.CharacterListCache
import com.example.data.comicvine.ComicVineClient
import com.example.data.comicvine.dto.ComicVineCharacterDto
import com.example.data.comicvine.mapper.toDomain
import com.example.domain.error.AppException.*
import com.example.domain.model.Character
import com.example.domain.repository.ComicVineRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException

class ComicVineRepositoryImpl(
    private val client: ComicVineClient,
    private val characterCache: CharacterCache,
    private val listCache : CharacterListCache,
    private val apiKey: String
) : ComicVineRepository {

    override suspend fun getCharacter(id:Int): Character {
        val cacheKey = "character:$id"

        characterCache.getIfPresent(cacheKey)?.let { return it }

        val character = callComicVine(notFoundId = id.toString()) {
            client.getResource<ComicVineCharacterDto>("character/4005-$id/") {
                parameter("api_key", apiKey)
                parameter("format", "json")
                parameter("field_list", "id,name,deck,description,image,publisher,count_of_issue_appearances,character_friends,character_enemies")
            }.results.toDomain()
        }
        characterCache.put(cacheKey, character)
        return character
    }

    override suspend fun searchCharacters(query: String,offset: Int): List<Character> {
        val normalizedQuery = query.trim().lowercase()
        val page = (offset / 10) + 1
        val cacheKey = "search:$normalizedQuery:$page"

        listCache.getIfPresent(cacheKey)?.let { return it }


        val characters = callComicVine {
            client.getResource<List<ComicVineCharacterDto>>("search/") {
                parameter("api_key", apiKey)
                parameter("format", "json")
                parameter("resources", "character")
                parameter("query", query)
                parameter("page", page)
            }.results.map { it.toDomain() }
        }

        listCache.put(cacheKey, characters)
        characters.forEach { characterCache.put("character:${it.id}", it) }
        return characters
    }

    override suspend fun getBrowseCharacters(offset: Int): List<Character> {
        val cacheKey = "characters:browse:$offset"
        listCache.getIfPresent(cacheKey)?.let { return it }

        val characters = callComicVine {
            client.getResource<List<ComicVineCharacterDto>>("characters/") {
                parameter("api_key", apiKey)
                parameter("format", "json")
                parameter("offset", offset)
                parameter("limit", 50)
            }.results.map { it.toDomain() }
        }

        listCache.put(cacheKey, characters)
        characters.forEach { characterCache.put("character:${it.id}", it) } // warm the detail cache too
        return characters
    }

    private suspend fun <T> callComicVine(notFoundId: String? = null, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.TooManyRequests -> throw ComicVineRateLimitException(
                    retryAfterSeconds = e.response.headers["Retry-After"]?.toLongOrNull()
                )
                HttpStatusCode.NotFound -> throw ResourceNotFoundException(notFoundId ?: "unknown")
                else -> throw ComicVineException("Comic Vine request failed", e)
            }
        } catch (e: ServerResponseException) {
            throw ComicVineException("Comic Vine server error (${e.response.status})", e)
        } catch (e: HttpRequestTimeoutException) {
            throw ComicVineException("Comic Vine request timed out", e)
        } catch (e: JsonConvertException) {
            // Comic Vine returns 200 + results: [] (instead of an object) when not found
            // that surfaces as a JsonConvertException
            throw ResourceNotFoundException(notFoundId ?: "unknown")
        }
    }
}