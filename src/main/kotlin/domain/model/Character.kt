package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val deck: String?,
    val description: String?,
    val imageUrl: String?,
    val publisherName: String?,
    val issueCount: Int,
    val friends: List<CharacterCredit>,
    val enemies: List<CharacterCredit>
)