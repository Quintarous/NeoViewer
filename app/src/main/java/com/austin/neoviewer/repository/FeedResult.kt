package com.austin.neoviewer.repository

import com.austin.neoviewer.database.FeedNeo

open class FeedResult {
    data class Success(val items: List<FeedNeo>): FeedResult()
    data class Error(val e: Exception): FeedResult()
}
