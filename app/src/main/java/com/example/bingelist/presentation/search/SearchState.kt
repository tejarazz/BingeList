package com.example.bingelist.presentation.search

import com.example.bingelist.domain.model.Drama

data class SearchState(
    val query: String = "",
    val results: List<Drama> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
