package com.austin.neoviewer.repository

import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface NeoRepositoryInterface {
    val requestInProgress: MutableStateFlow<Boolean>

    fun getPagingDataFlow(): Flow<PagingData<Neo>>

    suspend fun getErrorFlow(): Flow<FeedResult>

    suspend fun getFeedFlow(): Flow<List<FeedNeo>>

    suspend fun getNewFeedData(start: String, end: String)
}