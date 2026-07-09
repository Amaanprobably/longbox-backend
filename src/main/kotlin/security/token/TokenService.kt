package com.example.security.token

//will abstract out how our JWT Token Generation Logic works
interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}