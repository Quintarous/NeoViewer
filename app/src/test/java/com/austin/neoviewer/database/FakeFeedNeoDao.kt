package com.austin.neoviewer.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFeedNeoDao: FeedNeoDao {

    private val dataset = mutableListOf<FeedNeo>()

    override suspend fun insertAll(items: List<FeedNeo>) {
        dataset.addAll(items)
    }

    override fun getAll(): Flow<List<FeedNeo>> =
        flow { emit(dataset) }

    override fun clear() {
        dataset.clear()
    }
}