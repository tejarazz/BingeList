package com.example.bingelist.data.remote

import com.example.bingelist.data.remote.model.TmdbCreditsDto
import com.example.bingelist.data.remote.model.TmdbDetailsDto
import com.example.bingelist.data.remote.model.TmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DramaApi {
    @GET("search/tv")
    suspend fun searchDramas(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbSearchResponse

    @GET("tv/{series_id}")
    suspend fun getShowDetails(
        @Path("series_id") id: Int
    ): TmdbDetailsDto

    @GET("tv/{series_id}/credits")
    suspend fun getCredits(
        @Path("series_id") id: Int
    ): TmdbCreditsDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
