package com.austin.neoviewer.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class FakeNeoDao : NeoDao {

    val neoList = mutableListOf<Neo>()

    override suspend fun insertAll(items: List<Neo>) {
        insertAllBlocking(items)
    }

    fun insertAllBlocking(items: List<Neo>) {
        neoList.addAll(items)
    }

    override fun getAll(): List<Neo> = neoList

    override fun clearDatabase() = neoList.clear()
}