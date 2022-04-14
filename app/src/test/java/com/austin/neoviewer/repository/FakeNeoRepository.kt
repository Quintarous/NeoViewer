package com.austin.neoviewer.repository

import androidx.annotation.VisibleForTesting
import com.austin.neoviewer.database.Neo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

const val EXCEPTION_MESSAGE = "exception message"

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeNeoRepository: NeoRepositoryInterface {

    var hasData = false

    val neo1 = Neo(1, "name1", "designation1", "jpl_url1", false,
        1F, 1F, 1F, 1F,
        1F, 1F, 1F, 1F,
    )

    override suspend fun getBrowseResultFlow(): Flow<BrowseResult> {
        return if (hasData) {
            flow {
                emit(BrowseResult.Success(listOf(neo1)))
            }
        } else {
            flow {
                emit(BrowseResult.Error(Exception(EXCEPTION_MESSAGE)))
            }
        }
    }

    override suspend fun fetchMoreBrowseData() {
        TODO("Not yet implemented")
    }
}