package com.example.data.comicvine

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ComicVineRateLimiter(private val minIntervalMs: Long = 1200L) {
    private val mutex = Mutex()
    private var nextAllowedTime = 0L

    suspend fun acquire() {
        mutex.withLock {
            val now = System.currentTimeMillis()
            val waitTime = nextAllowedTime - now
            if (waitTime > 0) delay(waitTime)
            nextAllowedTime = maxOf(nextAllowedTime, System.currentTimeMillis()) + minIntervalMs
        }
    }
    suspend fun penalize(durationMs: Long) {
        mutex.withLock {
            val proposed = System.currentTimeMillis() + durationMs
            if (proposed > nextAllowedTime) nextAllowedTime = proposed
        }
    }
}