package com.austin.neoviewer.feed

import com.austin.neoviewer.repository.FakeNeoRepository
import com.austin.neoviewer.FakeSharedPreferences
import com.austin.neoviewer.repository.FeedResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Test

@ExperimentalCoroutinesApi
class FeedViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun whenErrorFlowEmits_CollectsError() {
        runTest {
            val viewModel = FeedViewModel(
                FakeNeoRepository().apply { shouldEmitError = true },
                FakeSharedPreferences(),
                dispatcher
            )

            val actual = viewModel.combinedFeedResultFlow.first()
            assert(actual.feedResult is FeedResult.Error)
        }
    }

    @Test
    fun whenFeedFlowEmits_CollectsData() {
        runTest {
            val viewModel = FeedViewModel(FakeNeoRepository(), FakeSharedPreferences(), dispatcher)

            val actual = viewModel.combinedFeedResultFlow.first()

            assert(actual.feedResult is FeedResult.Success)
        }
    }
}