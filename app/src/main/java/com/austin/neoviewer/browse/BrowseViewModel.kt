package com.austin.neoviewer.browse

import androidx.lifecycle.*
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.repository.BrowseResult
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "BrowseViewModel"

@HiltViewModel
class BrowseViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    val neoList: LiveData<BrowseResult> = liveData(dispatcher) {
        val browseResult = repository.getBrowseResultFlow().asLiveData()
        emitSource(browseResult)
    }

    fun getMoreBrowseData() {
        if (!repository.requestInProgress) {
            viewModelScope.launch(dispatcher) {
                repository.fetchMoreBrowseData()
            }
        }
    }

    fun retry() {
        viewModelScope.launch(dispatcher) {
            repository.retryBrowseDataFetch()
        }
    }
}