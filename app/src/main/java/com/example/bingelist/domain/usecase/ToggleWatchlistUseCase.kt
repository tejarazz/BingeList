package com.example.bingelist.domain.usecase

import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.repository.DramaRepository
import javax.inject.Inject

class ToggleWatchlistUseCase @Inject constructor(
    private val repository: DramaRepository
) {
    suspend operator fun invoke(drama: Drama) {
        repository.toggleWatchlistStatus(drama)
    }
}
