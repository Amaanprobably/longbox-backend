package com.example.data.comicvine.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComicVineResponse<T>(
    val error: String,
    val limit: Int,
    val offset: Int,
    @SerialName("number_of_page_results")
    val numberOfPageResults: Int,
    @SerialName("number_of_total_results")
    val numberOfTotalResults: Int,
    @SerialName("status_code")
    val statusCode: Int,
    val results: T
)