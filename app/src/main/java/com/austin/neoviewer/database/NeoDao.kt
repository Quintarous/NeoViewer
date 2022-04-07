package com.austin.neoviewer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@Dao
interface NeoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Neo>)

    @Query("SELECT * FROM Neo")
    fun getAll(): Flow<List<Neo>>

    @Query("SELECT * FROM Neo")
    fun getAllNonFlow(): List<Neo>

    @Query("DELETE FROM Neo")
    fun clearDatabase()
}