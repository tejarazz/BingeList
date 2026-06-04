package com.example.bingelist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dramas")
data class DramaEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Double,
    val description: String,
    val genre: String, // Room doesn't support List<String> directly, use CSV or TypeConverter
    val status: String,
    val year: Int,
    val sortOrder: Int = 0,
    val isInWatchlist: Boolean = false
)
