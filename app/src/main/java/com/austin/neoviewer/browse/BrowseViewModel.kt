package com.austin.neoviewer.browse

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    val pagingDataFlow = repository.getPagingDataFlow().cachedIn(viewModelScope)
}