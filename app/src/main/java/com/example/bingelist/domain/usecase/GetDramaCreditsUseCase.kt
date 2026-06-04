package com.example.bingelist.domain.usecase

import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.repository.DramaRepository
import javax.inject.Inject

class GetDramaCreditsUseCase @Inject constructor(
    private val repository: DramaRepository
) {
    suspend operator fun invoke(id: Int): List<Credit> {
        return repository.getDramaCredits(id)
    }
}
