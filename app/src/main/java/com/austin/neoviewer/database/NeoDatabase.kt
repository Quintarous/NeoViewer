package com.austin.neoviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Neo::class], version = 1)
abstract class NeoDatabase: RoomDatabase() {

    abstract fun getDao(): NeoDao

    companion object {

        private var INSTANCE: NeoDatabase? = null

        fun getInstance(context: Context): NeoDatabase {
            return INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): NeoDatabase {
            return Room.databaseBuilder(context, NeoDatabase::class.java, "NeoDatabase.db")
                .fallbackToDestructiveMigration()
                .build()

        }
    }
}