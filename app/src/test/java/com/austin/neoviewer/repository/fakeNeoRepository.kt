package com.austin.neoviewer.repository

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingData
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

    var hasData = false

    private val neo1 = Neo(1, "name1", "designation1", "jpl_url1", false,
        1F, 1F, 1F, 1F,
        1F, 1F, 1F, 1F,
    )
    private val neo2 = Neo(2, "name2", "designation2", "jpl_url2", true,
        2F, 2F, 2F, 2F,
        2F, 2F, 2F, 2F,
    )

    override fun getPagingDataFlow(): Flow<PagingData<Neo>> {
        TODO("Not yet implemented")
    }
}