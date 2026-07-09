package com.example.plugins

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.sentry.Sentry
import org.slf4j.event.Level
import java.util.UUID

fun Application.configureMonitoring() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")  // trace ID per request
        format { call ->
            "[${call.callId}] ${call.request.httpMethod.value} " +
                    "${call.request.uri} → ${call.response.status()}"
        }
    }
    Sentry.init { options ->
        options.dsn = System.getenv("SENTRY_DSN")
        options.isDebug = true
        options.tracesSampleRate = 1.0
    }
}