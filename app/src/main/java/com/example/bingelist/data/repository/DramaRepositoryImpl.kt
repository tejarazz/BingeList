package com.example.bingelist.data.repository

import android.util.Log
import androidx.paging.*
import com.example.bingelist.data.local.DramaDao
import com.example.bingelist.data.local.DramaDatabase
import com.example.bingelist.data.mapper.toCredit
import com.example.bingelist.data.mapper.toDrama
import com.example.bingelist.data.mapper.toEntity
import com.example.bingelist.data.remote.DramaApi
import com.example.bingelist.data.remote.paging.TmdbRemoteMediator
import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.repository.DramaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DramaRepositoryImpl @Inject constructor(
    private val api: DramaApi,
    private val dao: DramaDao,
    private val database: DramaDatabase
) : DramaRepository {

    override fun getWatchlist(): Flow<List<Drama>> {
        return dao.getWatchlist().map { entities ->
            entities.map { it.toDrama() }
        }
    }

    override suspend fun getDramaById(id: Int): Drama? = withContext(Dispatchers.IO) {
        dao.getDramaById(id)?.toDrama()
    }

    override suspend fun addDramaToWatchlist(drama: Drama) = withContext(Dispatchers.IO) {
        dao.insertDrama(drama.toEntity())
    }

    override suspend fun removeDramaFromWatchlist(drama: Drama) = withContext(Dispatchers.IO) {
        dao.deleteDrama(drama.toEntity())
    }

    override suspend fun toggleWatchlistStatus(drama: Drama) = withContext(Dispatchers.IO) {
        val currentStatus = drama.isInWatchlist
        val newStatus = !currentStatus
        
        val existing = dao.getDramaById(drama.id)
        if (existing == null) {
            dao.insertDrama(drama.copy(isInWatchlist = newStatus).toEntity())
        } else {
            dao.updateWatchlistStatus(drama.id, newStatus)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun searchDramas(query: String): Flow<PagingData<Drama>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = TmdbRemoteMediator(query, api, database),
            pagingSourceFactory = {
                dao.searchDramas(query)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDrama() }
        }
    }

    override suspend fun getDramaDetails(id: Int): Drama? = withContext(Dispatchers.IO) {
        try {
            val response = api.getShowDetails(id)
            response.toDrama()
        } catch (e: Exception) {
            Log.e("Repo", "Failed to fetch details for $id", e)
            null
        }
    }

    override suspend fun updateWatchlistOrder(dramas: List<Drama>) = withContext(Dispatchers.IO) {
        val entities = dramas.mapIndexed { index, drama ->
            drama.toEntity(sortOrder = index)
        }
        dao.updateDramas(entities)
    }

    override suspend fun getDramaCredits(id: Int): List<Credit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCredits(id)
            response.cast.map { it.toCredit() }
        } catch (e: Exception) {
            Log.e("Repo", "Failed to fetch credits for $id", e)
            emptyList()
        }
    }
}

