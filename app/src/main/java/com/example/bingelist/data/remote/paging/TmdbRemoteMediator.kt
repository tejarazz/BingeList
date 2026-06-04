package com.example.bingelist.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.bingelist.data.local.DramaDatabase
import com.example.bingelist.data.local.entity.DramaEntity
import com.example.bingelist.data.local.entity.RemoteKeys
import com.example.bingelist.data.mapper.toDrama
import com.example.bingelist.data.mapper.toEntity
import com.example.bingelist.data.remote.DramaApi
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TmdbRemoteMediator(
    private val query: String,
    private val api: DramaApi,
    private val database: DramaDatabase
) : RemoteMediator<Int, DramaEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DramaEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = api.searchDramas(query, page)
            val endOfPaginationReached = response.results.isEmpty()
            
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao.clearRemoteKeys()
                    database.dao.clearNonWatchlistDramas()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.results.map {
                    RemoteKeys(dramaId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao.insertAll(keys)
                database.dao.insertDramas(response.results.map { it.toDrama().toEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DramaEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { drama ->
                database.remoteKeysDao.remoteKeysDramaId(drama.id)
            }
    }

    private suspend fun getRemoteKeyClosestToPosition(state: PagingState<Int, DramaEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { dramaId ->
                database.remoteKeysDao.remoteKeysDramaId(dramaId)
            }
        }
    }
}
