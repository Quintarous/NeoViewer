package com.austin.neoviewer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Neo(
    @PrimaryKey
    val id: Int,
    val name: String,
    val designation: String,
    val jplUrl: String,
    val hazardous: Boolean,
    val kilometersDiamMin: Float,
    val kilometersDiamMax: Float,
    val metersDiamMin: Float,
    val metersDiamMax: Float,
    val milesDiamMin: Float,
    val milesDiamMax: Float,
    val feetDiamMin: Float,
    val feetDiamMax: Float,
    val date: String? = null
)
