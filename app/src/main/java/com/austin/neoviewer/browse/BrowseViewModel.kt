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

    // TODO have the scroll listener call a different method that checks the last item emitted on the neoList
    // LiveData and only makes a fetch request if it was NOT a BrowseResult.Error so we're not spamming
    // network requests and the only way for the user to retry is to hit the retry button
    fun getMoreBrowseData() {
        if (!repository.requestInProgress) {
            viewModelScope.launch(dispatcher) {
                repository.fetchMoreBrowseData()
            }
        }
    }
}