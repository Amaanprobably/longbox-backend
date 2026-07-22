package com.example.domain.error

sealed class AppException(message : String, cause: Throwable? = null) : Exception(message, cause) {
    class ComicVineException (message: String, cause: Throwable? = null) : AppException(message, cause)
    class ComicVineRateLimitException (val retryAfterSeconds: Long? = 60 * 60) : AppException(message = "Rate Limit Exceeded!! Wait for $retryAfterSeconds seconds.")
    class ResourceNotFoundException (resourceId : String) : AppException(message = "Resource not found with resourceId $resourceId")
    class AuthException (message: String) : AppException(message = message)
}