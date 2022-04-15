package com.austin.neoviewer.browse

import com.austin.neoviewer.database.Neo

sealed class BrowseItem {
    data class Holder(val neo: Neo): BrowseItem()
    data class Error(val message: String): BrowseItem()
}
