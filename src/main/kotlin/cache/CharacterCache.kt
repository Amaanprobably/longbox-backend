package com.example.cache

import com.example.domain.model.Character
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

typealias CharacterCache = Cache<String, Character>
typealias CharacterListCache = Cache<String, List<Character>>

fun createCharacterCache(): CharacterCache {
    return Caffeine.newBuilder()
        .maximumSize(2000)
        .expireAfterWrite(120, TimeUnit.MINUTES)
        .recordStats()
        .build()
}
fun createCharacterListCache(): CharacterListCache {
    return Caffeine.newBuilder()
        .maximumSize(200)
        .expireAfterWrite(60, TimeUnit.MINUTES) // matches Comic Vine's 200 requests/hour quota window
        .recordStats()
        .build()
}