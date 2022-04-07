package com.austin.neoviewer.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Neo::class], version = 1, exportSchema = false)
abstract class NeoDatabase: RoomDatabase() {

    abstract fun getDao(): NeoDao
}