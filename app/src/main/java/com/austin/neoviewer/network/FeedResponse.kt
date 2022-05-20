package com.austin.neoviewer.network

import com.google.gson.annotations.SerializedName

data class FeedResponse (
    @SerializedName("near_earth_objects")
    val days: Map<String, List<FeedNeoResponse>>
)