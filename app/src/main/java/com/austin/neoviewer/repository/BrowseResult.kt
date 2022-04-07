package com.austin.neoviewer.repository

import com.austin.neoviewer.database.Neo

open class BrowseResult {
    data class Success(val items: List<Neo>): BrowseResult()
    data class Error(val e: Exception): BrowseResult()
}
