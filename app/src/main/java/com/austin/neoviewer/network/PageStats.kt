package com.austin.neoviewer.network

import com.google.gson.annotations.SerializedName

data class PageStats(
    val size: Int,
    @SerializedName("total_elements") val totalElements: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val number: Int
)
