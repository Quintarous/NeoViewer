package com.austin.neoviewer.network

class FakeNeoService(
    private val browseResponse: BrowseResponse
) : NeoService {

    override suspend fun neoBrowse(page: Int): BrowseResponse = browseResponse
}