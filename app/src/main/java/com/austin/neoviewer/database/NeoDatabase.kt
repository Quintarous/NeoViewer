package com.austin.neoviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Neo::class, FeedNeo::class, RemoteKeys::class], version = 4, exportSchema = false)
abstract class NeoDatabase: RoomDatabase() {

    abstract fun getNeoDao(): NeoDao
    abstract fun getFeedNeoDao(): FeedNeoDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: NeoDatabase? = null

        fun getInMemoryInstance(context: Context): NeoDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.inMemoryDatabaseBuilder(
                    context,
                    NeoDatabase::class.java
                ).build().also { INSTANCE = it }
            }
        }
    }
}