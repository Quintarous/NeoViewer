package com.austin.neoviewer.repository

import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow

interface NeoRepositoryInterface {
    fun getPagingDataFlow(): Flow<PagingData<Neo>>

    fun getFeedFlow(): Flow<FeedResult>

    suspend fun getNewFeedData(start: String, end: String)
}