package com.example.data.comicvine

import com.example.data.comicvine.dto.ComicVineResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay

class ComicVineClient(
    val httpClient: HttpClient,
    val rateLimiter: ComicVineRateLimiter
) {
    suspend inline fun <reified T> getResource(
        url: String,
        maxRetries: Int = 4,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): ComicVineResponse<T> {
        repeat(maxRetries) { attempt ->
            rateLimiter.acquire()
            val response = httpClient.get(url) { block() }
            if (response.status.value == 420) {
                delay(2000L * (attempt + 1))
                return@repeat
            }
            return response.body()
        }
        throw ComicVineRateLimitException("Still rate-limited after $maxRetries retries")
    }
}

class ComicVineRateLimitException(message: String) : Exception(message)