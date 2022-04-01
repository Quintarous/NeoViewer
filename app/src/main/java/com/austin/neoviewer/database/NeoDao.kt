package com.austin.neoviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NeoDao {
    @Insert
    suspend fun insertAll(items: List<Neo>)

    @Query("SELECT * FROM Neo")
    fun getAll(): Flow<Neo>
}