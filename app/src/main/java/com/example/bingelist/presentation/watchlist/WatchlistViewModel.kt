package com.example.bingelist.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.usecase.AddDramaUseCase
import com.example.bingelist.domain.usecase.GetWatchlistUseCase
import com.example.bingelist.domain.usecase.UpdateWatchlistOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val getWatchlistUseCase: GetWatchlistUseCase,
    private val updateWatchlistOrderUseCase: UpdateWatchlistOrderUseCase,
    private val addDramaUseCase: AddDramaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    init {
        getWatchlist()
    }

    private fun getWatchlist() {
        getWatchlistUseCase()
            .onEach { dramas ->
                _state.update { it.copy(dramas = dramas) }
            }.launchIn(viewModelScope)
    }

    fun onMove(from: Int, to: Int) {
        val currentList = _state.value.dramas.toMutableList()
        if (from !in currentList.indices || to !in currentList.indices) return
        
        val item = currentList.removeAt(from)
        currentList.add(to, item)
        
        _state.update { it.copy(dramas = currentList) }
    }

    fun onDragFinished() {
        viewModelScope.launch(Dispatchers.IO) {
            updateWatchlistOrderUseCase(_state.value.dramas)
        }
    }

    fun onAddClick(drama: Drama) {
        viewModelScope.launch {
            try {
                addDramaUseCase(drama)
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun onRemoveClick(drama: Drama) {
        val previousDramas = _state.value.dramas
        // Optimistic Update
        _state.update { it.copy(dramas = it.dramas.filter { d -> d.id != drama.id }) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateWatchlistOrderUseCase(_state.value.dramas) // Save current order if needed
                // Since toggleWatchlistStatus is used for both add/remove in repo, 
                // but here we specifically want to remove or toggle
                addDramaUseCase(drama.copy(isInWatchlist = false))
            } catch (e: Exception) {
                // Rollback
                _state.update { it.copy(dramas = previousDramas) }
            }
        }
    }
}
