package com.example.bingelist.domain.usecase

import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.repository.DramaRepository
import javax.inject.Inject

class AddDramaUseCase @Inject constructor(
    private val repository: DramaRepository
) {
    suspend operator fun invoke(drama: Drama) {
        // Fetch full details (including rating) before adding to watchlist
        // since the search API doesn't provide the rating.
        val fullDetails = repository.getDramaDetails(drama.id)
        repository.addDramaToWatchlist(fullDetails ?: drama)
    }
}
