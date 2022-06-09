package com.austin.neoviewer.repository

import com.austin.neoviewer.database.FeedNeo

sealed class FeedResult {
    data class Success(val items: List<FeedResultItem>): FeedResult()
    data class Error(val e: Exception): FeedResult()
}

sealed class FeedResultItem {
    data class Neo(val neo: FeedNeo): FeedResultItem()
    data class Separator(val date: String): FeedResultItem()
}