package com.example.data.comicvine.mapper

import com.example.data.comicvine.dto.ComicVineCharacterDto
import com.example.domain.model.Character

fun ComicVineCharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        description = deck,
        imageUrl = image?.mediumUrl,
        publisherName = publisher?.name,
        issueCount = issueCount
    )
}