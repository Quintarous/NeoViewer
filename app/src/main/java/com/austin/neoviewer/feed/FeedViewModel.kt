package com.austin.neoviewer.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FeedViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    val feedFlow = liveData(dispatcher) {
        val result = repository.getFeedFlow().asLiveData()
        emitSource(result)
    }

    fun requestNewData(start: String, end: String) {
        viewModelScope.launch(dispatcher) {
            repository.getNewFeedData(start, end)
        }
    }
}