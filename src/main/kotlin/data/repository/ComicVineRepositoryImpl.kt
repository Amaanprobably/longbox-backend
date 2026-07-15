package com.example.data.repository

import com.example.cache.CharacterCache
import com.example.data.comicvine.ComicVineClient
import com.example.data.comicvine.dto.ComicVineCharacterDto
import com.example.data.comicvine.mapper.toDomain
import com.example.domain.model.Character
import com.example.domain.repository.ComicVineRepository
import io.ktor.client.request.*

class ComicVineRepositoryImpl(
    private val client: ComicVineClient,
    private val cache: CharacterCache,
    private val apiKey: String
) : ComicVineRepository {
    override suspend fun getCharacter(id:Int): Character {
        val cacheKey = "character:$id"

        cache.getIfPresent(cacheKey)?.let { return it }

        val response = client.getResource<ComicVineCharacterDto>("character/4005-$id/") {
            parameter("api_key", apiKey)
            parameter("format", "json")
        }

        val character = response.results.toDomain()
        cache.put(cacheKey, character)
        return character
    }
    override suspend fun searchCharacters(query: String): List<Character> {
        val response = client.getResource<List<ComicVineCharacterDto>>("search/") {
            parameter("api_key", apiKey)
            parameter("format", "json")
            parameter("resources", "character")
            parameter("query", query)
        }
        return response.results.map { it.toDomain() }
    }
}