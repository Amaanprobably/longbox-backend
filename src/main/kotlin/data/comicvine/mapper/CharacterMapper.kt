package com.example.data.comicvine.mapper

import com.example.data.comicvine.dto.ComicVineCharacterCreditDto
import com.example.data.comicvine.dto.ComicVineCharacterDto
import com.example.domain.model.Character
import com.example.domain.model.CharacterCredit

fun ComicVineCharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        deck = deck,
        description = stripHtml(description),
        imageUrl = image?.mediumUrl,
        publisherName = publisher?.name,
        issueCount = issueCount,
        friends = characterFriends.take(20).map { it.toDomain() },
        enemies = characterEnemies.take(20).map { it.toDomain() }
    )
}
fun ComicVineCharacterCreditDto.toDomain(): CharacterCredit {
    return CharacterCredit(
        id = id,
        name = name
    )
}
fun stripHtml(html: String?, maxParagraphs: Int? = 3): String? {
    if (html == null) return null

    val withBreaks = html.replace(
        Regex("</(p|h1|h2|h3|h4|figure)>", RegexOption.IGNORE_CASE),
        "\n"
    )
    val noTags = withBreaks.replace(Regex("<[^>]*>"), "")

    val paragraphs = noTags
        .replace(Regex("[ \t]+"), " ")
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val limited = if (maxParagraphs != null) paragraphs.take(maxParagraphs) else paragraphs

    return limited.joinToString("\n\n").takeIf { it.isNotBlank() }
}