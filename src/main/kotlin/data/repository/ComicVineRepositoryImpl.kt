package com.example.data.repository

import com.example.cache.CharacterCache
import com.example.data.comicvine.dto.ComicVineCharacterDto
import com.example.data.comicvine.dto.ComicVineResponse
import com.example.data.comicvine.mapper.toDomain
import com.example.domain.model.Character
import com.example.domain.repository.ComicVineRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ComicVineRepositoryImpl(
    private val httpClient: HttpClient,
    private val cache: CharacterCache,
    private val apiKey: String
) : ComicVineRepository {
    override suspend fun getCharacter(id:Int): Character {
        val cacheKey = "character:$id"

        cache.getIfPresent(cacheKey)?.let { return it }

        val response: ComicVineResponse<ComicVineCharacterDto> = httpClient
            .get("character/4005-$id/") {
                parameter("api_key", apiKey)
                parameter("format", "json")
            }
            .body()

        val character = response.results.toDomain()
        cache.put(cacheKey, character)
        return character
    }
    override suspend fun searchCharacters(query: String): List<Character> {
        val response: ComicVineResponse<List<ComicVineCharacterDto>> = httpClient
            .get("search/") {
                parameter("api_key", apiKey)
                parameter("format", "json")
                parameter("resources", "character")
                parameter("query", query)
            }
            .body()

        return response.results.map { it.toDomain() }
    }
}