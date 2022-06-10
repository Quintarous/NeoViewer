package com.austin.neoviewer.feed

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.austin.neoviewer.R
import com.austin.neoviewer.launchFragmentInHiltContainer
import com.austin.neoviewer.network.FakeNeoService
import com.austin.neoviewer.network.NeoService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class FeedFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        (fakeNeoService as FakeNeoService).neoFeedThrowException()
    }

    @Inject
    lateinit var fakeNeoService: NeoService

    @Test
    fun feedFragment_withData_DisplaysCorrectly() {
        runTest(UnconfinedTestDispatcher()) {
            (fakeNeoService as FakeNeoService).neoFeedReturnData()
            launchFragmentInHiltContainer<FeedFragment>()

            onView(withId(R.id.select_date_range_label)).perform(click())
            onView(withText("SAVE")).perform(click())

            onView(withId(R.id.feed_recycler)).check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView

                assert(recyclerView.adapter?.itemCount == 4)
            }
        }
    }

    @Test
    fun feedFragment_withEmptyData_DisplaysNoDataTextView() {
        runTest(UnconfinedTestDispatcher()) {
            (fakeNeoService as FakeNeoService).neoFeedReturnNoData()
            launchFragmentInHiltContainer<FeedFragment>()

            onView(withId(R.id.select_date_range_label)).perform(click())
            onView(withText("SAVE")).perform(click())

            onView(withId(R.id.feed_recycler)).check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView

                assert(recyclerView.adapter?.itemCount == 0)
            }
        }
    }

    @Test
    fun feedFragment_withException_DisplaysCorrectly() {
        runTest(UnconfinedTestDispatcher()) {
            launchFragmentInHiltContainer<FeedFragment>()

            onView(withId(R.id.select_date_range_label)).perform(click())
            onView(withText("SAVE")).perform(click())

            onView(withId(R.id.feed_recycler)).check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView

                assert(recyclerView.adapter?.itemCount == 0)
            }
        }
    }
}