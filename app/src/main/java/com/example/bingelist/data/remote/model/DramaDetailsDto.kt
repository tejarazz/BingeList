package com.example.bingelist.data.remote.model

import com.google.gson.annotations.SerializedName

data class DramaDetailsResponse(
    @SerializedName("tvShow") val drama: DramaDetailsDto
)

data class DramaDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val title: String?,
    @SerializedName("image_thumbnail_path") val imageUrl: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("status") val status: String?,
    @SerializedName("start_date") val startDate: String?
)
