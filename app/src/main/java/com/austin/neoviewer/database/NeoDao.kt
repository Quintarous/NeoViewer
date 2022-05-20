package com.austin.neoviewer.database

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@Dao
interface NeoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Neo>)

    @Query("SELECT * FROM Neo")
    fun getAll(): PagingSource<Int, Neo>

    @Query("DELETE FROM Neo")
    fun clearNeo()
}