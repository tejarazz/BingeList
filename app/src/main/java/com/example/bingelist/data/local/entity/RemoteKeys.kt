package com.example.bingelist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val dramaId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
