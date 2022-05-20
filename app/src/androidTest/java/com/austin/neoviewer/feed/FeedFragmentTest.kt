package com.austin.neoviewer.feed

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FeedFragmentTest {

    @get:Rule
    private val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }
}