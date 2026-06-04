package com.example.bingelist.data.remote.model

import com.google.gson.annotations.SerializedName

data class TmdbCreditsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cast") val cast: List<TmdbCastDto>,
    @SerializedName("crew") val crew: List<TmdbCrewDto>
)

data class TmdbCastDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("character") val character: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class TmdbCrewDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String,
    @SerializedName("profile_path") val profilePath: String?
)
