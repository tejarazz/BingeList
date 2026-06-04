package com.example.bingelist.presentation.watchlist

import com.example.bingelist.domain.model.Drama

data class WatchlistState(
    val dramas: List<Drama> = emptyList(),
    val isLoading: Boolean = false
)
