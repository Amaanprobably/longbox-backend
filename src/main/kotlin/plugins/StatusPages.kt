package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.sentry.Sentry
import io.sentry.SentryLevel
import com.example.domain.error.AppException.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            Sentry.withScope { scope ->
                scope.setTag("callId", call.callId ?: "unknown")
                scope.setTag("path", call.request.path())
                scope.level = when (cause) {
                    is ComicVineRateLimitException -> SentryLevel.WARNING
                    is AuthException -> SentryLevel.WARNING
                    is ResourceNotFoundException -> SentryLevel.WARNING
                    is ComicVineException -> SentryLevel.ERROR
                    else -> SentryLevel.ERROR
                }
                Sentry.captureException(cause)
            }
            call.respondText(text = "500: ${cause.message}" , status = HttpStatusCode.InternalServerError)
        }
    }
}
