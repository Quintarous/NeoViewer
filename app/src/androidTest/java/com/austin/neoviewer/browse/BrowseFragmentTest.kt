package com.austin.neoviewer.browse

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.austin.neoviewer.R
import com.austin.neoviewer.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import com.austin.neoviewer.repository.FakeNeoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import javax.inject.Inject

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class BrowseFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fakeRepo: FakeNeoRepository

    @Before
    fun setup() {
        hiltRule.inject()
        fakeRepo.hasData = true
    }

    @Test
    fun browseFragment_GivenData_DisplaysCorrectly() {
        runTest {
            launchFragmentInHiltContainer<BrowseFragment>()
            onView(withId(R.id.browse_recycler)).check(matches(isDisplayed()))
            onView(withText("name1")).check(matches(isDisplayed()))
        }
    }
}