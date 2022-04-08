package com.austin.neoviewer.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.neoviewer.repository.NeoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BrowseViewModel"

@HiltViewModel
class BrowseViewModel @Inject constructor (
    private val repository: NeoRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    init {
        Log.i(TAG, "started")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBrowseResultFlow().collect {
                Log.i(TAG, "$it")
            }
        }
    }
}