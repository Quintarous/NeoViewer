package com.austin.neoviewer.browse

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

private const val TAG = "BrowseViewModel"

@HiltViewModel
class BrowseViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    val pagingDataFlow = repository.getPagingDataFlow().cachedIn(viewModelScope)
}