package com.austin.neoviewer.database

class FakeFeedNeoDao: FeedNeoDao {

    private val dataset = mutableListOf<FeedNeo>()

    override suspend fun insertAll(items: List<FeedNeo>) {
        dataset.addAll(items)
    }

    override fun getAll(): List<FeedNeo> = dataset

    override fun clear() {
        dataset.clear()
    }
}