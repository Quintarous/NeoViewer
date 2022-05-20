package com.austin.neoviewer.network

import com.google.gson.annotations.SerializedName

data class FeedNeoResponse(
    val id: Int,
    val name: String,
    @SerializedName("nasa_jpl_url") val jplUrl: String,
    @SerializedName("is_potentially_hazardous_asteroid") val hazardous: Boolean,
    @SerializedName("estimated_diameter") val diameterData: DiameterData
)
