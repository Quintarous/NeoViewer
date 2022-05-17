package com.austin.neoviewer.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.NeoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

private const val TAG = "NeoRepository"

class NeoRepository (
    private val service: NeoService,
    private val neoDatabase: NeoDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NeoRepositoryInterface {
    private val neoDao = neoDatabase.getNeoDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingDataFlow(): Flow<PagingData<Neo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 120,
                enablePlaceholders = false // TODO experiment with setting this to true
            ),
            remoteMediator = NeoRemoteMediator(service, neoDatabase),
            pagingSourceFactory = { neoDao.getAll() }
        ).flow
    }
}