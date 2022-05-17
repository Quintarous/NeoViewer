package com.austin.neoviewer.database

import androidx.room.*

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKeys(keys: List<RemoteKeys>)

    @Query("SELECT * FROM RemoteKeys WHERE neoId = :neoId")
    suspend fun remoteKeysByNeoId(neoId: Int): RemoteKeys?

    @Query("DELETE FROM RemoteKeys")
    fun clearRemoteKeys()
}