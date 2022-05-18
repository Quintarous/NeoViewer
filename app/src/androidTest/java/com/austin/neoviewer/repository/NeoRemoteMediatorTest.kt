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

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class NeoRemoteMediatorTest {

    private val fakeService = FakeNeoService()
    private val db = NeoDatabase.getInMemoryInstance(ApplicationProvider.getApplicationContext())

    @After
    fun cleanup() {
        fakeService.throwException()
        db.clearAllTables()
    }

    @Test
    fun refreshLoadReturnsSuccessWhenDataIsPresent() {
        runTest {
            fakeService.returnWithData()
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
            assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }
    }

    @Test
    fun refreshLoadReturnsEndOfPaginationWhenDataIsMissing() {
        runTest {
            fakeService.returnWithoutData()
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
            assert((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }
    }

    @Test
    fun refreshLoadReturnsErrorWhenExceptionIsThrown() {
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

            assert(result is RemoteMediator.MediatorResult.Error)
        }
    }
}