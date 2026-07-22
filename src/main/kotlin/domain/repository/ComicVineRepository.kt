package com.example.domain.repository

import com.example.domain.model.Character

interface ComicVineRepository {
    suspend fun getCharacter(id: Int): Character
    suspend fun searchCharacters(query: String,offset: Int): List<Character>
    suspend fun getBrowseCharacters(offset: Int): List<Character>
}