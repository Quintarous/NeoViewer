package com.austin.neoviewer.repository

import androidx.paging.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.FakeNeoService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
@MediumTest
class NeoRemoteMediatorTest {

    // creating our fake retrofit service and in memory database instance
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
            fakeService.returnWithData() // telling the fake api to return good data
            val remoteMediator = NeoRemoteMediator(fakeService, db)

            // perform a refresh load
            val result = remoteMediator.load(
                LoadType.REFRESH,
                PagingState<Int, Neo>(
                    listOf(),
                    null,
                    PagingConfig(20),
                    20
                )
            )

            // assert the load is successful and data was present (ie: endOfPaginationReached is false)
            assertThat(result, `is`(instanceOf(RemoteMediator.MediatorResult.Success::class.java)))
            assertThat(
                (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
                equalTo(false)
            )
        }
    }

    @Test
    fun refreshLoadReturnsEndOfPaginationWhenDataIsMissing() {
        runTest {
            fakeService.returnWithoutData() // telling the fake api to return empty data
            val remoteMediator = NeoRemoteMediator(fakeService, db)

            // triggering the load
            val result = remoteMediator.load(
                LoadType.REFRESH,
                PagingState<Int, Neo>(
                    listOf(),
                    null,
                    PagingConfig(20),
                    20
                )
            )

            // asserting the load was successful and there was no data (ie: endOfPaginationReached is true)
            assertThat(result, instanceOf(RemoteMediator.MediatorResult.Success::class.java))
            assertThat(
                (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
                equalTo(true)
            )
        }
    }

    @Test
    fun refreshLoadReturnsErrorWhenExceptionIsThrown() {
        runTest {
            // the fake api defaults to throwing an exception
            val remoteMediator = NeoRemoteMediator(fakeService, db)

            // triggering the load
            val result = remoteMediator.load(
                LoadType.REFRESH,
                PagingState<Int, Neo>(
                    listOf(),
                    null,
                    PagingConfig(20),
                    20
                )
            )

            // asserting the exception was handled and an error was returned
            assertThat(result, instanceOf(RemoteMediator.MediatorResult.Error::class.java))
        }
    }
}