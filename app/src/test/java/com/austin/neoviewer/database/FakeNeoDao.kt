package com.austin.neoviewer.database

import kotlinx.coroutines.flow.Flow

class FakeNeoDao : NeoDao {

    val neoList = mutableListOf<Neo>()

    override suspend fun insertAll(items: List<Neo>) {
        insertAllBlocking(items)
    }

    fun insertAllBlocking(items: List<Neo>) {
        neoList.addAll(items)
    }

    override fun getAll(): Flow<Neo> {
        TODO("Not yet implemented")
    }
}