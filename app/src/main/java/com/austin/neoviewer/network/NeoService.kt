package com.austin.neoviewer.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"
const val API_KEY = "TZIMsTB3ztsDc6fdqEyTqGtNy7Dr7Goqe5L1xvvC"
const val STARTING_PAGE = 0

interface NeoService {
    @GET("neo/browse?api_key=TZIMsTB3ztsDc6fdqEyTqGtNy7Dr7Goqe5L1xvvC")
    suspend fun neoBrowse(
        @Query("page") page: Int
    ): BrowseResponse

    @GET("feed?api_key=TZIMsTB3ztsDc6fdqEyTqGtNy7Dr7Goqe5L1xvvC")
    fun neoFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<FeedResponse>
}