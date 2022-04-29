package com.austin.neoviewer

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.austin.neoviewer.browse.BrowseFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.austin.neoviewer.repository.FakeNeoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
//TODO get the launchFragmentInHiltContainer function from the hilt architecture sample
    @Test
    fun browseFragment_GivenData_DisplaysCorrectly() {
        runTest {
            launchFragmentInHiltContainer<BrowseFragment>()
            onView(withId(R.id.browse_recycler)).check(matches(isDisplayed()))
            onView(withText("name1")).check(matches(isDisplayed()))
        }
    }
}