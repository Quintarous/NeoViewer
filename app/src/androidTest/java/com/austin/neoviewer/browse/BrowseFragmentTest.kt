package com.austin.neoviewer.browse

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.austin.neoviewer.R
import com.austin.neoviewer.launchFragmentInHiltContainer
import com.austin.neoviewer.network.FakeNeoService
import com.austin.neoviewer.network.NeoService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import com.austin.neoviewer.repository.FakeNeoRepository
import dagger.hilt.android.testing.BindValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import javax.inject.Inject

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class BrowseFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Inject
    lateinit var fakeNeoService: NeoService

    @Test
    fun browseFragment_GivenData_DisplaysCorrectly() {
        runTest {
            (fakeNeoService as FakeNeoService).returnWithData()
            launchFragmentInHiltContainer<BrowseFragment>()

            onView(withId(R.id.browse_recycler)).check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView
                assert(recyclerView.adapter?.itemCount == 2)
            }
        }
    }

    @Test
    fun browseFragment_givenError_ShowsRetryButton() {
        runTest {
            launchFragmentInHiltContainer<BrowseFragment>()

            onView(withId(R.id.browse_frag_retry_button)).check(matches(isDisplayed()))
        }
    }
}