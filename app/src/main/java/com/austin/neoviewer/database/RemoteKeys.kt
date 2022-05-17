package com.austin.neoviewer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(
    @PrimaryKey
    val neoId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
