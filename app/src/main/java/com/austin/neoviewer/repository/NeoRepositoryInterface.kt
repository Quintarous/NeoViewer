package com.austin.neoviewer.repository

import kotlinx.coroutines.flow.Flow

interface NeoRepositoryInterface {
    suspend fun getBrowseResultFlow(): Flow<BrowseResult>

    suspend fun fetchMoreBrowseData()
}