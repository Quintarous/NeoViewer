package com.austin.neoviewer.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.network.FeedNeoResponse
import com.austin.neoviewer.network.FeedResponse
import com.austin.neoviewer.network.NeoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.*
import java.io.IOException

private const val TAG = "NeoRepository"
// TODO cancel the retrofit request and start a new one when a new request is made
class NeoRepository (
    private val service: NeoService,
    private val neoDatabase: NeoDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NeoRepositoryInterface {
    private val neoDao = neoDatabase.getNeoDao()
    private val feedNeoDao = neoDatabase.getFeedNeoDao()

    override val requestInProgress = MutableStateFlow(false)

    private val feedFlow = MutableSharedFlow<FeedResult>(replay = 1)

    private var call: Call<FeedResponse>? = null


    override suspend fun getFeedFlow(): Flow<FeedResult> = feedFlow


    // getting the paging data flow for the BrowseFragment
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


    // makes a network request for feed data and caches it in the db
    private suspend fun cacheFeedData(
        start: String,
        end: String
    ) {
        requestInProgress.emit(true)

        try {
            call = service.neoFeed(start, end)
            val processedFeedNeos = mutableListOf<FeedNeo>()

            val response = call?.execute()
            response?.let {
                if (response.isSuccessful) {
                    for (day in response.body()!!.days) {
                        processedFeedNeos.addAll(processFeedData(day.key, day.value))
                    }
                } else {
                    feedFlow.emit(FeedResult.Error(Exception(response.errorBody().toString())))
                }
            }

            feedNeoDao.clear()
            feedNeoDao.insertAll(processedFeedNeos)
            emitCachedFeedData()
        } catch(e: IOException) {
            Log.i(TAG, "catch: $e")
            feedFlow.emit(FeedResult.Error(e))
        } catch(e: HttpException) {
            Log.i(TAG, "catch: $e")
            feedFlow.emit(FeedResult.Error(e))
        } catch (e: Exception) {
            Log.i(TAG, "catch: $e")
            feedFlow.emit(FeedResult.Error(e))
        }

        requestInProgress.emit(false)
        call = null
    }


    // used by the FeedViewModel to get updated data
    override suspend fun getNewFeedData(start: String, end: String) {
        if (requestInProgress.value) { // if request is in progress
            call?.let { // and call is not null and not canceled
                if (!it.isCanceled) { // then cancel and nullify it
                    it.cancel()
                    call = null
                    requestInProgress.emit(false)
                }
            }
        }
        cacheFeedData(start, end) // before finally starting a new request
    }


    private suspend fun emitCachedFeedData() {
        feedFlow.emit(FeedResult.Success(feedNeoDao.getAll()))
    }


    private suspend fun emitThrowable(t: Throwable) {
        feedFlow.emit(FeedResult.Error(Exception(t)))
    }


    // the feed data returned by the retrofit service needs to be processed from nested data classes
    // to a bunch of primitives to be inserted into the database
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