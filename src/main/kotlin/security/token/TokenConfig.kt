package com.example.security.token

data class TokenConfig(
    // who issued
    val issuer: String,
    // meant for whom
    val audience: String,
    // expiration time
    val expiresIn: Long,
    // 256 bit secret that only the server should know
    val secret: String
)
