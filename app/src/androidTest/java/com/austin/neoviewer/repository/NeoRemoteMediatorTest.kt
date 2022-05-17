package com.austin.neoviewer.repository

import android.util.Log
import androidx.paging.*
import androidx.test.core.app.ApplicationProvider
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.FakeNeoService
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NeoRemoteMediatorTest {

    private val fakeService = FakeNeoService()
    private val db = NeoDatabase.getInMemoryInstance(ApplicationProvider.getApplicationContext())

    @Test
    @OptIn(ExperimentalPagingApi::class)
    fun refreshLoadReturnsSuccessWhenDataIsPresent() {
        runTest {
            val remoteMediator = NeoRemoteMediator(fakeService, db)

            val result = remoteMediator.load(
                LoadType.REFRESH,
                PagingState<Int, Neo>(
                    listOf(),
                    null,
                    PagingConfig(20),
                    20
                )
            )

            assert(result is RemoteMediator.MediatorResult.Success)
        }
    }

    @After
    fun cleanup() {
        fakeService.throwException()
        db.clearAllTables()
    }
}