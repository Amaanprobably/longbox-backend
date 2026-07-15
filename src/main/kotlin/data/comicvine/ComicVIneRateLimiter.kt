package com.example.data.comicvine

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ComicVineRateLimiter(private val minIntervalMs: Long = 1200L) {
    private val mutex = Mutex()
    private var lastRequestTime = 0L

    suspend fun acquire() {
        mutex.withLock {
            val elapsed = System.currentTimeMillis() - lastRequestTime
            if (elapsed < minIntervalMs) delay(minIntervalMs - elapsed)
            lastRequestTime = System.currentTimeMillis()
        }
    }
}