package com.austin.neoviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedNeoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedNeo>)

    @Query("SELECT * FROM FeedNeo ORDER BY date ASC")
    fun getAll(): Flow<List<FeedNeo>>

    @Query("DELETE FROM FeedNeo")
    fun clear()
}