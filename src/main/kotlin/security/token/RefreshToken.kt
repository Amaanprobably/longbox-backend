package com.example.security.token

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

fun generateRefreshToken(): String {
    return SecureRandom.getInstanceStrong()
        .let { random ->
            ByteArray(64).also { random.nextBytes(it) }
        }
        .let { Base64.getUrlEncoder().encodeToString(it) }
}
fun hashRefreshToken(token: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(token.toByteArray())
        .let { Base64.getEncoder().encodeToString(it) }
}