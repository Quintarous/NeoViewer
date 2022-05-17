package com.austin.neoviewer.network

import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.network.BrowseResponse
import com.austin.neoviewer.network.NeoService

class FakeNeoService() : NeoService {

    private var browseResponse: BrowseResponse? = null

    private val neoResponse1 = NeoResponse(
        1,
        "neo1",
        "designation",
        "jplUrl",
        false,
        DiameterData(
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
        )
    )
    private val emptyBrowseResponse = BrowseResponse(
        PageStats(
            20,
            29364,
            1469,
            0
        ),
        listOf()
    )
    private val populatedBrowseResponse = BrowseResponse(
        PageStats(
            20,
            29364,
            1469,
            0
        ),
        listOf(neoResponse1)
    )

    override suspend fun neoBrowse(page: Int): BrowseResponse = browseResponse ?: throw Exception()

    fun throwException() { browseResponse = null }

    fun returnWithoutData() { browseResponse = emptyBrowseResponse }

    fun returnWithData() { browseResponse = populatedBrowseResponse }
}