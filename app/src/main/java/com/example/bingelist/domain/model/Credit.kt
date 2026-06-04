package com.example.bingelist.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Credit(
    val id: Int,
    val name: String,
    val characterOrJob: String,
    val imageUrl: String?
)
