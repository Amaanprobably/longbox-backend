package com.example.data.comicvine.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComicVineCharacterDto(
    val id: Int,
    val name: String,
    val deck: String? = null,
    val image: ComicVineImageDto? = null,
    val publisher: ComicVinePublisherDto? = null,
    @SerialName("count_of_issue_appearances")
    val issueCount: Int = 0
)

@Serializable
data class ComicVineImageDto(
    @SerialName("medium_url")
    val mediumUrl: String? = null
)

@Serializable
data class ComicVinePublisherDto(
    val name: String? = null
)