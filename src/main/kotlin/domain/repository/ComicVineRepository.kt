package com.example.domain.repository

import com.example.domain.model.Character

interface ComicVineRepository {
    suspend fun getCharacter(id: Int): Character
    suspend fun searchCharacters(query: String): List<Character>
}