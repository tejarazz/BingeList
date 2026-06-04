package com.example.bingelist.data.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bingelist.data.mapper.toDrama
import com.example.bingelist.data.remote.DramaApi
import com.example.bingelist.domain.model.Drama

class DramaPagingSource(
    private val api: DramaApi,
    private val query: String
) : PagingSource<Int, Drama>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Drama> {
        val page = params.key ?: 1
        return try {
            val response = api.searchDramas(query, page)
            val dramas = response.results.map { it.toDrama() }
            Log.d("PagingSource", "Received ${dramas.size} dramas for query: $query, page: $page")
            
            LoadResult.Page(
                data = dramas,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (dramas.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Drama>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
