package com.austin.neoviewer.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDatabase
import com.austin.neoviewer.database.RemoteKeys
import com.austin.neoviewer.network.NeoResponse
import com.austin.neoviewer.network.NeoService
import com.austin.neoviewer.network.STARTING_PAGE
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NeoRemoteMediator(
    private val service: NeoService,
    private val neoDatabase: NeoDatabase
): RemoteMediator<Int, Neo>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Neo>
    ): MediatorResult {
        // calculate the page number to be loaded based on the LoadType
        val page: Int = when(loadType) {
            LoadType.APPEND -> {
                val remoteKeys = remoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }

            LoadType.PREPEND -> {
                val remoteKeys = remoteKeysForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.REFRESH -> STARTING_PAGE
        }

        return try {
            val browseResponse = service.neoBrowse(page) // get the response from the network
            Log.i("bruh", "page: $page browseResponse: ${browseResponse.items}")
            val endOfPaginationReached = browseResponse.items.isEmpty()
            // create all the RemoteKeys for this page of Neo objects
            val prevKey = if (page == STARTING_PAGE) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val newRemoteKeys = browseResponse.items.map {
                RemoteKeys(it.id, prevKey, nextKey)
            }

            neoDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) { // clear the db on a refresh
                    neoDatabase.clearAllTables()
                }
                // insert the processed Neo objects and RemoteKeys into the database
                neoDatabase.getNeoDao().insertAll(processNeoResponse(browseResponse.items))
                Log.i("bruh", "database updated")
                neoDatabase.getRemoteKeysDao().insertKeys(newRemoteKeys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }


    private suspend fun remoteKeysForLastItem(state: PagingState<Int, Neo>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.last()?.let { neo ->
            neoDatabase.getRemoteKeysDao().remoteKeysByNeoId(neo.id)
        }
    }

    private suspend fun remoteKeysForFirstItem(state: PagingState<Int, Neo>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.last()?.let { neo ->
            neoDatabase.getRemoteKeysDao().remoteKeysByNeoId(neo.id)
        }
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