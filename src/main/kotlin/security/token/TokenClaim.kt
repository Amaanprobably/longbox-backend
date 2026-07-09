package com.example.security.token

// key value pair used to store information in a token
data class TokenClaim(
    //key
    val name: String,
    //value
    val value: String,
)
