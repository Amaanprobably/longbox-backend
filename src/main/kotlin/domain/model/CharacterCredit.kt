package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterCredit(
    val id: Int,
    val name: String
)