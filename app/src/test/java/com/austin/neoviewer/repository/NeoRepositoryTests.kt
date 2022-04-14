package com.austin.neoviewer.repository

import com.austin.neoviewer.database.FakeNeoDao
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NeoRepositoryTests {

    private lateinit var repository: NeoRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val neoResponse1 = NeoResponse(
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
    private val neoResponse2 = NeoResponse(
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
    private val neoResponse3 = NeoResponse(
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
    private val neoResponseList = listOf(neoResponse1, neoResponse2, neoResponse3)

    private val browseResponse = BrowseResponse(
        PageStats(3, 29089, 1455, 112),
        neoResponseList
    )
    private val fakeService = FakeNeoService(browseResponse)

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
        ),
    )

    // creating an instance of the repository to test
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

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
            fakeService,
            fakeNeoDao,
            testDispatcher
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }


    @Test
    fun getBrowseResultFlow_EmitsSuccess() {
        runTest {
            val result = repository.getBrowseResultFlow().first()
            assert(result is BrowseResult.Success)
            result as BrowseResult.Success
            assert(result.items == expectedData)
        }
    }
}