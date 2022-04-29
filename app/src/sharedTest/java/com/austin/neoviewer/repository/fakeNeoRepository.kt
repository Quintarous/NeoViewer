package com.austin.neoviewer.repository

import androidx.annotation.VisibleForTesting
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import javax.inject.Inject

const val EXCEPTION_MESSAGE = "hasData set to false so no data for you!"

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeNeoRepository @Inject constructor(): NeoRepositoryInterface {

    override var requestInProgress: Boolean = false
    var hasData = false

    private val neo1 = Neo(1, "name1", "designation1", "jpl_url1", false,
        1F, 1F, 1F, 1F,
        1F, 1F, 1F, 1F,
    )
    private val neo2 = Neo(2, "name2", "designation2", "jpl_url2", true,
        2F, 2F, 2F, 2F,
        2F, 2F, 2F, 2F,
    )

    private val browseResultFlow = MutableSharedFlow<BrowseResult>(replay = 1)

    override suspend fun getBrowseResultFlow(): Flow<BrowseResult> {
        return if (hasData) {
            browseResultFlow.emit(BrowseResult.Success(listOf(neo1)))
            browseResultFlow
        } else {
            browseResultFlow.emit(BrowseResult.Error(Exception(EXCEPTION_MESSAGE)))
            browseResultFlow
        }
    }

    override suspend fun retryBrowseDataFetch() {
        browseResultFlow.emit(BrowseResult.Success(listOf(neo1, neo2)))
    }

    override suspend fun fetchMoreBrowseData() {
        browseResultFlow.emit(BrowseResult.Success(listOf(neo1, neo2)))
    }
}