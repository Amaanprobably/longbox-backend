package com.example.domain.model

data class Character(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val publisherName: String?,
    val issueCount: Int
)