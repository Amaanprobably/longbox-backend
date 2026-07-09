package com.example.data.user

import com.example.data.model.User
import com.example.security.token.hashRefreshToken
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import com.mongodb.client.model.Updates
import org.bson.types.ObjectId

class MongoUserDataSource(
    db : MongoDatabase
) : UserDataSource {
    private val users = db.getCollection<User>("users")
    override suspend fun getUserByUsername(username: String): User? {
       return users.find(eq("username",username)).firstOrNull()
    }

    override suspend fun getUserByRefreshToken(refreshToken: String): User? {
        return users.find(eq("refreshToken", hashRefreshToken( refreshToken))).firstOrNull()
    }
    override suspend fun saveRefreshToken(userId: ObjectId, refreshToken: String, refreshTokenExpiresAt:Long): Boolean {
        return users.updateOne(
            eq("_id", userId),
            Updates.combine(
                Updates.set("refreshToken", refreshToken),
                Updates.set("refreshTokenExpiresAt", refreshTokenExpiresAt)
            )
        ).wasAcknowledged()
    }
    override suspend fun revokeRefreshToken(userId: ObjectId): Boolean {
        return users.updateOne(
            eq("_id", userId),
            Updates.combine(
                Updates.set("refreshToken", null),
                Updates.set("refreshTokenExpiresAt", null)
            )
        ).wasAcknowledged()
    }
    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
}