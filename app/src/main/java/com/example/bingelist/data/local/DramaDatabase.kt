package com.example.bingelist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bingelist.data.local.entity.DramaEntity
import com.example.bingelist.data.local.entity.RemoteKeys

@Database(entities = [DramaEntity::class, RemoteKeys::class], version = 4, exportSchema = false)
abstract class DramaDatabase : RoomDatabase() {
    abstract val dao: DramaDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "bingelist_db"
    }
}
