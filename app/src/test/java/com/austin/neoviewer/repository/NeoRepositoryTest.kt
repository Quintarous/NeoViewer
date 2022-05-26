package com.austin.neoviewer.repository

import com.austin.neoviewer.database.*
import com.austin.neoviewer.network.FakeNeoService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class NeoRepositoryTest {

    // creating our fake and mock dependencies
    private val fakeService = FakeNeoService()
    private val mockNeoDao = mock<NeoDao>()
    private val mockDb = mock<NeoDatabase> {
        on { this.getNeoDao() } doReturn mockNeoDao
        on { this.getFeedNeoDao() } doReturn FakeFeedNeoDao()
    }

    @Before
    fun setup() {
        fakeService.neoFeedThrowException() // we'll have the fake api default to throwing an exception
    }


    @Test
    fun cacheFeedData_givenData_InsertsIntoDatabase() {
        fakeService.neoFeedReturnData() // this test will be with a good data return from the api

        val expectedData = listOf(
            FeedNeo(1, "neo1", "jplUrl", true, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, "2022-04-19"),
            FeedNeo(2, "neo2", "jplUrl", false, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, "2022-04-20")
        )

        val repository = NeoRepository(fakeService, mockDb)

        // for the test itself we'll request data then assert the result emitted actually has data
        runTest(UnconfinedTestDispatcher()) {
            val feedFlow = repository.getErrorFlow()

            repository.getNewFeedData("", "")

            val firstValue = feedFlow.first()
            assert(firstValue == FeedResult.Success(expectedData))
        }
    }

    @Test
    fun cacheFeedData_givenEmptyData_EmitsEmptySuccess() {
        fakeService.neoFeedReturnNoData() // this test is for an empty but successful api return

        val repository = NeoRepository(fakeService, mockDb)

        // requesting data and asserting an empty success result is emitted
        runTest(UnconfinedTestDispatcher()) {
            val feedFlow = repository.getErrorFlow()

            repository.getNewFeedData("", "")

            val firstValue = feedFlow.first()
            assert(firstValue == FeedResult.Success(listOf<FeedNeo>()))
        }
    }

    @Test
    fun cacheFeedData_givenException_EmitsError() { // testing for proper error handling

        val repository = NeoRepository(fakeService, mockDb)

        // requesting data as usual and asserting an error result is emitted
        runTest(UnconfinedTestDispatcher()) {
            val feedFlow = repository.getErrorFlow()

            repository.getNewFeedData("", "")

            val firstValue = feedFlow.first()
            assert(firstValue is FeedResult.Error)
        }
    }
}