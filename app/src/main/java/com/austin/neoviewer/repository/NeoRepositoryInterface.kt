package com.austin.neoviewer.repository

import kotlinx.coroutines.flow.Flow

interface NeoRepositoryInterface {

    var requestInProgress: Boolean

    suspend fun getBrowseResultFlow(): Flow<BrowseResult>

    suspend fun fetchMoreBrowseData()

    suspend fun retryBrowseDataFetch()
}