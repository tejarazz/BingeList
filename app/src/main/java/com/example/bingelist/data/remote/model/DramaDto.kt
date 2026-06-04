package com.example.bingelist.data.remote.model

import com.google.gson.annotations.SerializedName

data class DramaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val title: String?,
    @SerializedName("image_thumbnail_path") val imageUrl: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("status") val status: String?,
    @SerializedName("start_date") val startDate: String?
)

data class DramaSearchResponse(
    @SerializedName("total") val total: String,
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("tv_shows") val dramas: List<DramaDto>
)
