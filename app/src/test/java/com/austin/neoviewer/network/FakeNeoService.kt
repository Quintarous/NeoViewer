package com.austin.neoviewer.network

import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.network.BrowseResponse
import com.austin.neoviewer.network.NeoService
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

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


    private val feedNeoResponse1 = FeedNeoResponse(
        1,
        "neo1",
        "jplUrl",
        true,
        DiameterData(
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
        )
    )
    private val feedNeoResponse2 = FeedNeoResponse(
        2,
        "neo2",
        "jplUrl",
        false,
        DiameterData(
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
            DiameterValues(1F, 1F),
        )
    )

    private val feedResponse = FeedResponse(
        days = mapOf(
            Pair("2022-04-19", listOf(feedNeoResponse1)),
            Pair("2022-04-20", listOf(feedNeoResponse2))
        )
    )

    private val emptyFeedResponse = FeedResponse(mapOf())

    private var neoFeedReturnValue: Call<FeedResponse>? = null

    override fun neoFeed(startDate: String, endDate: String): Call<FeedResponse> =
        neoFeedReturnValue ?: throw IOException()

    fun neoFeedReturnData() {
        neoFeedReturnValue = mock<Call<FeedResponse>> {
            on { execute() } doReturn Response.success(feedResponse)
        }
    }

    fun neoFeedReturnNoData() {
        neoFeedReturnValue = mock {
            on { execute() } doReturn Response.success(emptyFeedResponse)
        }
    }

    fun neoFeedThrowException() {
        neoFeedReturnValue = null
    }
}