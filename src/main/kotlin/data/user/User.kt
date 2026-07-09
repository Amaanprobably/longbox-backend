package com.example.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val salt: String,
    val refreshToken: String? = null,
    val refreshTokenExpiresAt: Long? = null,
    @BsonId val id: ObjectId = ObjectId(),
)
