package com.example.bingelist.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class Drama(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double,
    val description: String,
    val genre: ImmutableList<String>,
    val status: String, // e.g., "Plan to Watch", "Watching", "Completed"
    val year: Int,
    val isInWatchlist: Boolean = false
)
