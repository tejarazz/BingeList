package com.example.bingelist.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.domain.usecase.SearchDramasUseCase
import com.example.bingelist.domain.usecase.ToggleWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchDramasUseCase: SearchDramasUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val results: Flow<PagingData<Drama>> = _query
        .debounce(500L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            searchDramasUseCase(query)
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onToggleWatchlist(drama: Drama) {
        viewModelScope.launch {
            toggleWatchlistUseCase(drama)
        }
    }
}
