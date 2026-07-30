package com.example.domain.repository

import com.example.data.user.User
import org.bson.types.ObjectId

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserByRefreshToken(refreshToken: String): User?
    suspend fun saveRefreshToken(userId: ObjectId, refreshToken: String, refreshTokenExpiresAt:Long): Boolean
    suspend fun revokeRefreshToken(userId: ObjectId): Boolean
    suspend fun insertUser(user: User): Boolean
}