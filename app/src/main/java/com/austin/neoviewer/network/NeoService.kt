package com.austin.neoviewer.network

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"
private const val API_KEY = "TZIMsTB3ztsDc6fdqEyTqGtNy7Dr7Goqe5L1xvvC"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface NeoService {
    @GET("neo/browse?api_key=$API_KEY")
    suspend fun neoBrowse(
        @Query("page") page: Int
    ): BrowseResponse
}

object NeoApi {
    val neoService: NeoService by lazy {
        retrofit.create(NeoService::class.java)
    }
}