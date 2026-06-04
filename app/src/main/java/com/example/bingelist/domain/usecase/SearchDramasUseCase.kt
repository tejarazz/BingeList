package com.example.bingelist.domain.usecase

import androidx.paging.PagingData
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.repository.DramaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchDramasUseCase @Inject constructor(
    private val repository: DramaRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Drama>> {
        if (query.isBlank()) return flowOf(PagingData.empty())
        return repository.searchDramas(query)
    }
}
