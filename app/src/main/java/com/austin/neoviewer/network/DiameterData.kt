package com.austin.neoviewer.network

import com.google.gson.annotations.SerializedName

data class DiameterData(
    val kilometers: DiameterValues,
    val meters: DiameterValues,
    val miles: DiameterValues,
    val feet: DiameterValues,
)

data class DiameterValues(
    @SerializedName("estimated_diameter_min") val diameterMin: Float,
    @SerializedName("estimated_diameter_max") val diameterMax: Float
)
