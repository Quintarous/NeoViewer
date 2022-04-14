package com.austin.neoviewer.network

import androidx.annotation.VisibleForTesting

//@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
class FakeNeoService(private val browseResponse: BrowseResponse) : NeoService {

    override suspend fun neoBrowse(page: Int): BrowseResponse = browseResponse
}