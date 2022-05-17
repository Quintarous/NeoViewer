package com.austin.neoviewer.network

import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"
const val API_KEY = "TZIMsTB3ztsDc6fdqEyTqGtNy7Dr7Goqe5L1xvvC"
const val STARTING_PAGE = 1

interface NeoService {
    @GET("neo/browse?api_key=$API_KEY")
    suspend fun neoBrowse(
        @Query("page") page: Int
    ): BrowseResponse
}