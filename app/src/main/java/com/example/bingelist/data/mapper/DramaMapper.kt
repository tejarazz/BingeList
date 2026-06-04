package com.example.bingelist.data.mapper

import com.example.bingelist.data.local.entity.DramaEntity
import com.example.bingelist.data.remote.DramaApi
import com.example.bingelist.data.remote.model.TmdbCastDto
import com.example.bingelist.data.remote.model.TmdbDetailsDto
import com.example.bingelist.data.remote.model.TmdbDramaDto
import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.model.Drama
import kotlinx.collections.immutable.toImmutableList

fun TmdbCastDto.toCredit(): Credit {
    return Credit(
        id = id,
        name = name,
        characterOrJob = character,
        imageUrl = profilePath?.let { "${DramaApi.IMAGE_BASE_URL}$it" }
    )
}

fun TmdbDramaDto.toDrama(): Drama {
    return Drama(
        id = id,
        title = name ?: "Unknown Title",
        imageUrl = posterPath?.let { "${DramaApi.IMAGE_BASE_URL}$it" } ?: "",
        rating = voteAverage ?: 0.0,
        description = overview ?: "No description available",
        genre = emptyList<String>().toImmutableList(), // TMDB search only gives genre IDs
        status = "Unknown",
        year = firstAirDate?.split("-")?.firstOrNull()?.toIntOrNull() ?: 0,
        isInWatchlist = false
    )
}

fun TmdbDetailsDto.toDrama(): Drama {
    return Drama(
        id = id,
        title = name ?: "Unknown Title",
        imageUrl = posterPath?.let { "${DramaApi.IMAGE_BASE_URL}$it" } ?: "",
        rating = voteAverage ?: 0.0,
        description = overview ?: "No description available",
        genre = genres?.map { it.name }?.toImmutableList() ?: emptyList<String>().toImmutableList(),
        status = status ?: "Unknown",
        year = firstAirDate?.split("-")?.firstOrNull()?.toIntOrNull() ?: 0,
        isInWatchlist = false
    )
}

fun DramaEntity.toDrama(): Drama {
    return Drama(
        id = id,
        title = title,
        imageUrl = imageUrl,
        rating = rating,
        description = description,
        genre = genre.split(",").filter { it.isNotBlank() }.toImmutableList(),
        status = status,
        year = year,
        isInWatchlist = isInWatchlist
    )
}

fun Drama.toEntity(sortOrder: Int = 0): DramaEntity {
    return DramaEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        rating = rating,
        description = description,
        genre = genre.joinToString(","),
        status = status,
        year = year,
        sortOrder = sortOrder,
        isInWatchlist = isInWatchlist
    )
}
