package com.austin.neoviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Neo::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class NeoDatabase: RoomDatabase() {

    abstract fun getNeoDao(): NeoDao
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