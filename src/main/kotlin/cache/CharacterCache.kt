package com.example.cache

import com.example.domain.model.Character
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

typealias CharacterCache = Cache<String, Character>
typealias CharacterListCache = Cache<String, List<Character>>

fun createCharacterCache(): CharacterCache {
    return Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(120, TimeUnit.MINUTES)
        .build()
}
fun createCharacterListCache(): CharacterListCache {
    return Caffeine.newBuilder()
        .maximumSize(200)
        .expireAfterWrite(60, TimeUnit.MINUTES) // Same as rate-limit duration
        .build()
}