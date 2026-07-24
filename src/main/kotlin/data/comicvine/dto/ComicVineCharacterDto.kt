package com.example.data.comicvine.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComicVineCharacterDto(
    val id: Int,
    val name: String,
    val deck: String? = null,
    val description:String?= null,
    val image: ComicVineImageDto? = null,
    val publisher: ComicVinePublisherDto? = null,
    @SerialName("count_of_issue_appearances")
    val issueCount: Int = 0,
    @SerialName("character_friends")
    val characterFriends: List<ComicVineCharacterCreditDto> = emptyList(),
    @SerialName("character_enemies")
    val characterEnemies: List<ComicVineCharacterCreditDto> = emptyList()
)

@Serializable
data class ComicVineCharacterCreditDto(
    val id: Int,
    val name: String,
    @SerialName("api_detail_url")
    val apiDetailUrl: String? = null
)

@Serializable
data class ComicVineImageDto(
    @SerialName("original_url")
    val mediumUrl: String? = null
)

@Serializable
data class ComicVinePublisherDto(
    val name: String? = null
)