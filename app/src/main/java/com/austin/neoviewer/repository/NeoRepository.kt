package com.austin.neoviewer.repository

import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDao
import com.austin.neoviewer.network.NeoResponse
import com.austin.neoviewer.network.NeoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "NeoRepository"

class NeoRepository (
    private val service: NeoService,
    private val neoDao: NeoDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NeoRepositoryInterface {

    private var currentPage: Int = 0

    private var requestInProgress: Boolean = false

    private val browseResultFlow = MutableSharedFlow<BrowseResult>(replay = 1)


    override suspend fun getBrowseResultFlow(): Flow<BrowseResult> {
        if (currentPage == 0) {
            withContext(dispatcher) {
                neoDao.clearDatabase()
                cacheBrowseData()
            }
        } else {
            emitCachedData()
        }
        return browseResultFlow
    }


    override suspend fun fetchMoreBrowseData() {
        currentPage++
        if (requestInProgress) {
            withContext(dispatcher) { cacheBrowseData() }
        }
    }


    private suspend fun cacheBrowseData(): Boolean {
        requestInProgress = true
        var success = false

        try {
            val networkResponse = service.neoBrowse(currentPage) // get updated data from network
            // process data into a list of Neo objects to be stored in the db
            val processedResponse: List<Neo> = processNeoResponse(networkResponse.items)
            neoDao.insertAll(processedResponse) // insert them into the db
            emitCachedData() // emit the data into the browseResultFlow
            success = true
        } catch (e: IOException) {
            browseResultFlow.emit(BrowseResult.Error(e))
        } catch (e: HttpException) {
            browseResultFlow.emit(BrowseResult.Error(e))
        }
        requestInProgress = false
        return success
    }


    private suspend fun emitCachedData() {
        browseResultFlow.emit(BrowseResult.Success(neoDao.getAll()))
    }


    private fun processNeoResponse(input: List<NeoResponse>): List<Neo> {
        val output = mutableListOf<Neo>()
        for (neo in input) {
            output.add(
                Neo(
                    neo.id,
                    neo.name,
                    neo.designation,
                    neo.jplUrl,
                    neo.hazardous,
                    neo.diameterData.kilometers.diameterMin,
                    neo.diameterData.kilometers.diameterMax,
                    neo.diameterData.meters.diameterMin,
                    neo.diameterData.meters.diameterMax,
                    neo.diameterData.miles.diameterMin,
                    neo.diameterData.miles.diameterMax,
                    neo.diameterData.feet.diameterMin,
                    neo.diameterData.feet.diameterMax,
                )
            )
        }
        return output
    }
}