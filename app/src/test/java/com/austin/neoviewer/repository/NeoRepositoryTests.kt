package com.austin.neoviewer.repository

import com.austin.neoviewer.database.FakeNeoDao
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.network.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NeoRepositoryTests {

    private lateinit var neoResponseList: List<NeoResponse>
    private lateinit var repository: NeoRepository

    private val expectedData = listOf(
        Neo(1, "name1", "designation1", "jpl_url1", false,
            1F, 1F, 1F, 1F,
            1F, 1F, 1F, 1F,
        ),
        Neo(2, "name2", "designation2", "jpl_url2", false,
            2F, 2F, 2F, 2F,
            2F, 2F, 2F, 2F,
        ),
        Neo(3, "name3", "designation3", "jpl_url3", true,
            3F, 3F, 3F, 3F,
            3F, 3F, 3F, 3F,
        )
    )

    // creating an instance of the repository to test
    @Before
    fun createRepository() {
        val neoResponse1 = NeoResponse(
            1,
            "name1",
            "designation1",
            "jpl_url1",
            false,
            DiameterData(
                DiameterValues(1F, 1F),
                DiameterValues(1F, 1F),
                DiameterValues(1F, 1F),
                DiameterValues(1F, 1F)
            )
        )
        val neoResponse2 = NeoResponse(
            2,
            "name2",
            "designation2",
            "jpl_url2",
            false,
            DiameterData(
                DiameterValues(2F, 2F),
                DiameterValues(2F, 2F),
                DiameterValues(2F, 2F),
                DiameterValues(2F, 2F)
            )
        )
        val neoResponse3 = NeoResponse(
            3,
            "name3",
            "designation3",
            "jpl_url3",
            true,
            DiameterData(
                DiameterValues(3F, 3F),
                DiameterValues(3F, 3F),
                DiameterValues(3F, 3F),
                DiameterValues(3F, 3F)
            )
        )
        neoResponseList = listOf(neoResponse1, neoResponse2, neoResponse3)

        val browseResponse = BrowseResponse(
            PageStats(3, 29089, 1455, 112),
            neoResponseList
        )

        // building a FakeNeoDao with some data for testing
        val fakeNeoDao = FakeNeoDao().apply {
            this.insertAllBlocking(listOf(
                Neo(1, "name1", "designation1", "jpl_url1", false,
                    1F, 1F, 1F, 1F,
                    1F, 1F, 1F, 1F,
                )
            ))
        }

        repository = NeoRepository(
            FakeNeoService(browseResponse),
            fakeNeoDao,
            StandardTestDispatcher()
        )
    }


    @Test
    fun getBrowseResultFlow_updatesCacheFromNetwork() {
        runTest {
            val result = repository.getBrowseResultFlow().first()
            assert(result is BrowseResult.Success)
            result as BrowseResult.Success
            assert(result.items == expectedData)
        }
    }
}