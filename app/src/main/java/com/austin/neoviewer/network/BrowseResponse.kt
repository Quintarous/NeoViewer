package com.austin.neoviewer.network

import com.google.gson.annotations.SerializedName

data class BrowseResponse(
    @SerializedName("page") val pageStats: PageStats,
    @SerializedName("near_earth_objects") val items: List<NeoResponse>
)