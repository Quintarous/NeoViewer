package com.austin.neoviewer.repository

import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import okio.IOException

class FakeNeoRepository: NeoRepositoryInterface {

    val sampleFeedNeo = FeedNeo(
        1,
        "sample",
        "jplUrl",
        false,
        1F,
        1F,
        1F,
        1F,
        1F,
        1F,
        1F,
        1F,
        "2022-6-9"
    )

    var shouldEmitError = false

    override val requestInProgress: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false)

    override fun getPagingDataFlow(): Flow<PagingData<Neo>> {
        TODO()
    }

    override suspend fun getErrorFlow(): Flow<FeedResult> =
        if (shouldEmitError) {
            flow<FeedResult> { emit(FeedResult.Error(IOException("test io exception"))) }
        } else {
            flow<FeedResult> {}
        }

    override suspend fun getFeedFlow(): Flow<List<FeedNeo>> =
        if (!shouldEmitError) {
            flow { emit(listOf(sampleFeedNeo)) }
        } else {
            flow {}
        }

    override suspend fun getNewFeedData(start: String, end: String) {
        TODO("Not yet implemented")
    }
}