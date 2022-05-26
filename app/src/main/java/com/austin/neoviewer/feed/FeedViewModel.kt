package com.austin.neoviewer.feed

import androidx.lifecycle.*
import com.austin.neoviewer.repository.FeedResult
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FeedViewModel"

@HiltViewModel
class FeedViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val savedState: SavedStateHandle
): ViewModel() {

    init {
        viewModelScope.launch(dispatcher) {
            val feedFlow = repository.getFeedFlow().collectLatest {
                if (repository.requestInProgress.value) {
                    repository.requestInProgress.emit(false)
                }

                _combinedFeedResultFlow.emit(FeedResult.Success(it))
            }
            val errorFlow = repository.getErrorFlow().collectLatest {
                if (repository.requestInProgress.value) {
                    repository.requestInProgress.emit(false)
                }

                _combinedFeedResultFlow.emit(it)
            }
        }
    }

    private val _combinedFeedResultFlow = MutableSharedFlow<FeedResult>(replay = 1)
    val combinedFeedResultFlow: SharedFlow<FeedResult>
        get() = _combinedFeedResultFlow

    // collecting load status flow and exposing it to the UI
    val isRequestInProgress = liveData(dispatcher) {
        emitSource(repository.requestInProgress.asLiveData())
    }

    fun requestNewData(start: String, end: String) {
        viewModelScope.launch(dispatcher) {
            repository.getNewFeedData(start, end)
        }
    }
}