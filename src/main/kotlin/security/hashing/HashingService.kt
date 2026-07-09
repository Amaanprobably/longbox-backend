package com.example.security.hashing

interface HashingService {
    fun generateSaltedHash(value : String, saltLength: Int = 32): SaltedHash
    fun verify(password: String, saltedHash : SaltedHash) : Boolean
}