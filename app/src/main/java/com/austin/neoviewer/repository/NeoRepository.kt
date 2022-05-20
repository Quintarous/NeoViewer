package com.austin.neoviewer.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.FeedNeoResponse
import com.austin.neoviewer.network.NeoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "NeoRepository"

class NeoRepository (
    private val service: NeoService,
    private val neoDatabase: NeoDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NeoRepositoryInterface {
    private val neoDao = neoDatabase.getNeoDao()
    private val feedNeoDao = neoDatabase.getFeedNeoDao()

    var requestInProgress: Boolean = false

    private val feedFlow = MutableSharedFlow<FeedResult>(replay = 1)

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingDataFlow(): Flow<PagingData<Neo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 120,
                enablePlaceholders = false
            ),
            remoteMediator = NeoRemoteMediator(service, neoDatabase),
            pagingSourceFactory = { neoDao.getAll() }
        ).flow
    }


    override fun getFeedFlow(): Flow<FeedResult> = feedFlow

    private suspend fun cacheFeedData(
        start: String,
        end: String
    ) {
        if (requestInProgress) {
            return
        }
        requestInProgress = true

        try {
            val response = service.neoFeed(start, end)
            val processedFeedNeos = mutableListOf<FeedNeo>()

            for (day in response.days) {
                processedFeedNeos.addAll(processFeedData(day.key, day.value))
            }
            feedNeoDao.clear()
            if(processedFeedNeos.isNotEmpty()) feedNeoDao.insertAll(processedFeedNeos)
            emitCachedFeedData()
        } catch(e: IOException) {
            feedFlow.emit(FeedResult.Error(e))
        } catch(e: HttpException) {
            feedFlow.emit(FeedResult.Error(e))
        }

        requestInProgress = false
    }

    override suspend fun getNewFeedData(start: String, end: String) {
        cacheFeedData(start, end)
    }

    private suspend fun emitCachedFeedData() {
        feedFlow.emit(FeedResult.Success(feedNeoDao.getAll()))
    }

    private fun processFeedData(day: String, list: List<FeedNeoResponse>): List<FeedNeo> {
        val outputList = mutableListOf<FeedNeo>()
        for (response in list) {
            outputList.add(FeedNeo(
                id = response.id,
                name = response.name,
                jplUrl = response.jplUrl,
                hazardous = response.hazardous,
                kilometersDiamMin = response.diameterData.kilometers.diameterMin,
                kilometersDiamMax = response.diameterData.kilometers.diameterMax,
                metersDiamMin = response.diameterData.meters.diameterMin,
                metersDiamMax = response.diameterData.meters.diameterMax,
                milesDiamMin = response.diameterData.miles.diameterMin,
                milesDiamMax = response.diameterData.miles.diameterMax,
                feetDiamMin = response.diameterData.feet.diameterMin,
                feetDiamMax = response.diameterData.feet.diameterMax,
                date = day
            ))
        }
        return outputList
    }
}