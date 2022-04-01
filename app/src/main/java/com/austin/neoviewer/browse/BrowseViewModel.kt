package com.austin.neoviewer.browse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.network.BrowseResponse
import com.austin.neoviewer.network.NeoApi
import kotlinx.coroutines.launch

private const val TAG = "BrowseViewModel"

class BrowseViewModel: ViewModel() {
    val service = NeoApi.neoService

    init {
        viewModelScope.launch {
            val result = service.neoBrowse(112)
            Log.d(TAG, "$result")
        }
    }
}