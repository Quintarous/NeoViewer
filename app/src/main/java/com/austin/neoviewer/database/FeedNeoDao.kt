package com.austin.neoviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.SharedFlow

@Dao
interface FeedNeoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedNeo>)

    @Query("SELECT * FROM FeedNeo")
    fun getAll(): List<FeedNeo>

    @Query("DELETE FROM FeedNeo")
    fun clear()
}