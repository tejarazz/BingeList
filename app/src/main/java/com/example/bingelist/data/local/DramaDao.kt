package com.example.bingelist.data.local

import androidx.room.*
import com.example.bingelist.data.local.entity.DramaEntity
import com.example.bingelist.data.local.entity.RemoteKeys
import kotlinx.coroutines.flow.Flow

@Dao
interface DramaDao {
    @Query("SELECT * FROM dramas WHERE isInWatchlist = 1 ORDER BY sortOrder ASC")
    fun getWatchlist(): Flow<List<DramaEntity>>

    @Query("SELECT * FROM dramas WHERE title LIKE '%' || :query || '%'")
    fun searchDramas(query: String): androidx.paging.PagingSource<Int, DramaEntity>

    @Query("UPDATE dramas SET isInWatchlist = :isInWatchlist WHERE id = :id")
    suspend fun updateWatchlistStatus(id: Int, isInWatchlist: Boolean)

    @Query("SELECT * FROM dramas WHERE id = :id")
    suspend fun getDramaById(id: Int): DramaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrama(drama: DramaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDramas(dramas: List<DramaEntity>)

    @Update
    suspend fun updateDramas(dramas: List<DramaEntity>)

    @Delete
    suspend fun deleteDrama(drama: DramaEntity)

    @Query("DELETE FROM dramas WHERE isInWatchlist = 0")
    suspend fun clearNonWatchlistDramas()

    @Query("DELETE FROM dramas")
    suspend fun clearAllDramas()
}

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE dramaId = :dramaId")
    suspend fun remoteKeysDramaId(dramaId: Int): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
