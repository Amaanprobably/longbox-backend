package com.example.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class CacheStatsDto(
    val hitRate: Double,
    val missRate: Double,
    val hitCount: Long,
    val missCount: Long,
    val evictionCount: Long,
    val estimatedSize: Long
)

@Serializable
data class AllCacheStatsDto(
    val characterCache: CacheStatsDto,
    val listCache: CacheStatsDto
)