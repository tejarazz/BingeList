package com.example.bingelist.domain.usecase

import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.repository.DramaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistUseCase @Inject constructor(
    private val repository: DramaRepository
) {
    operator fun invoke(): Flow<List<Drama>> {
        return repository.getWatchlist()
    }
}
