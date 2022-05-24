package com.austin.neoviewer.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface NeoRepositoryInterface {
    val requestInProgress: MutableStateFlow<Boolean>

    fun getPagingDataFlow(): Flow<PagingData<Neo>>

    suspend fun getFeedFlow(): Flow<FeedResult>

    suspend fun getNewFeedData(start: String, end: String)
}