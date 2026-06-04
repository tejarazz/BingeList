package com.example.bingelist.domain.repository

import androidx.paging.PagingData
import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.model.Drama
import kotlinx.coroutines.flow.Flow

interface DramaRepository {
    fun getWatchlist(): Flow<List<Drama>>
    suspend fun getDramaById(id: Int): Drama?
    suspend fun addDramaToWatchlist(drama: Drama)
    suspend fun removeDramaFromWatchlist(drama: Drama)
    suspend fun toggleWatchlistStatus(drama: Drama)
    fun searchDramas(query: String): Flow<PagingData<Drama>>
    suspend fun getDramaDetails(id: Int): Drama?
    suspend fun updateWatchlistOrder(dramas: List<Drama>)
    suspend fun getDramaCredits(id: Int): List<Credit>
}

