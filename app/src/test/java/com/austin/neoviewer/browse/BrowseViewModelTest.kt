package com.austin.neoviewer.browse

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.getOrAwaitValue
import com.austin.neoviewer.observeForTesting
import com.austin.neoviewer.repository.BrowseResult
import com.austin.neoviewer.repository.EXCEPTION_MESSAGE
import com.austin.neoviewer.repository.FakeNeoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BrowseViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeRepo = FakeNeoRepository()
    private lateinit var viewModel: BrowseViewModel

    private val neo1 = Neo(1, "name1", "designation1", "jpl_url1", false,
        1F, 1F, 1F, 1F,
        1F, 1F, 1F, 1F,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BrowseViewModel(fakeRepo, testDispatcher)
    }

    @After
    fun cleanup() {
        fakeRepo.hasData = false
        Dispatchers.resetMain()
    }


    @Test
    fun givenData_EmitsDataInNeoList() {
        fakeRepo.hasData = true
        runTest {
            val expected = BrowseResult.Success(listOf(neo1))
            val actual = viewModel.neoList.getOrAwaitValue()
            assert(expected == actual)
        }
    }

    @Test
    fun givenError_EmitsErrorInNeoList() {
        runTest {
            val actual = viewModel.neoList.getOrAwaitValue()
            assert(actual is BrowseResult.Error)
        }
    }
}