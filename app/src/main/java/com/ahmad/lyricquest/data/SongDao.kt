package com.ahmad.lyricquest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY addedAt DESC")
    fun all(): Flow<List<Song>>

    @Insert
    suspend fun insert(song: Song): Long

    @Delete
    suspend fun delete(song: Song)
}
