package com.example.bingelist.data.remote.model

import com.google.gson.annotations.SerializedName

data class TmdbSearchResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbDramaDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TmdbDramaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class TmdbDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("genres") val genres: List<TmdbGenreDto>?
)

data class TmdbGenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
