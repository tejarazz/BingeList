package com.example.bingelist.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.usecase.GetDramaCreditsUseCase
import com.example.bingelist.domain.usecase.GetDramaDetailsUseCase
import com.example.bingelist.domain.usecase.RemoveDramaUseCase
import com.example.bingelist.domain.usecase.ToggleWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDramaDetailsUseCase: GetDramaDetailsUseCase,
    private val getDramaCreditsUseCase: GetDramaCreditsUseCase,
    private val removeDramaUseCase: RemoveDramaUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dramaId: Int = savedStateHandle.get<Int>("dramaId") ?: -1
    private val initialTitle: String = savedStateHandle.get<String>("title") ?: ""
    private val initialImageUrl: String = savedStateHandle.get<String>("imageUrl") ?: ""
    private val initialRating: Float = savedStateHandle.get<Float>("rating") ?: 0f

    private val _state = MutableStateFlow<DetailState>(
        DetailState.Success(
            drama = Drama(
                id = dramaId,
                title = initialTitle,
                imageUrl = initialImageUrl,
                rating = initialRating.toDouble(),
                description = "",
                genre = kotlinx.collections.immutable.persistentListOf(),
                status = "",
                year = 0
            ),
            isInitialData = true
        )
    )
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        if (dramaId != -1) {
            getDramaDetails(dramaId)
        }
    }

    private fun getDramaDetails(id: Int) {
        viewModelScope.launch {
            // Fetching extended details and credits concurrently
            val detailsDeferred = async { getDramaDetailsUseCase(id) }
            val creditsDeferred = async { getDramaCreditsUseCase(id) }

            val extendedDrama = detailsDeferred.await()
            val credits = creditsDeferred.await()

            if (extendedDrama != null) {
                _state.value = DetailState.Success(
                    drama = extendedDrama,
                    credits = credits,
                    isInitialData = false
                )
            } else {
                // If deep fetch fails, we still have the initial data, but maybe show an error or keep partial
                // For now, only fail if we had nothing (which shouldn't happen here)
            }
        }
    }

    fun toggleWatchlist(drama: Drama) {
        val currentState = _state.value
        if (currentState is DetailState.Success) {
            // Optimistic Update
            val updatedDrama = drama.copy(isInWatchlist = !drama.isInWatchlist)
            _state.value = currentState.copy(drama = updatedDrama)

            viewModelScope.launch {
                try {
                    toggleWatchlistUseCase(drama)
                } catch (e: Exception) {
                    // Rollback
                    _state.value = currentState
                }
            }
        }
    }

    fun deleteDrama(drama: Drama, onDeleted: () -> Unit) {
        viewModelScope.launch {
            removeDramaUseCase(drama)
            onDeleted()
        }
    }
}

sealed class DetailState {
    object Loading : DetailState()
    data class Success(
        val drama: Drama,
        val credits: List<Credit> = emptyList(),
        val isInitialData: Boolean = false
    ) : DetailState()
    data class Error(val message: String) : DetailState()
}
