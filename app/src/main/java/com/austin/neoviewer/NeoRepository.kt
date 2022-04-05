package com.austin.neoviewer

import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.database.NeoDao
import com.austin.neoviewer.network.NeoResponse
import com.austin.neoviewer.network.NeoService
import javax.inject.Inject

// TODO setup dependency injection and write unit tests before continuing!
class NeoRepository @Inject constructor (
    private val service: NeoService,
    private val neoDao: NeoDao) {

    private val currentPage: Int = 0

    private var requestInProgress: Boolean = false

    // TODO test this method
    private suspend fun updateDatabase(): Boolean {
        requestInProgress = true
        var success = false

        try {
            val networkResponse = service.neoBrowse(currentPage) // get updated data from network
            // process data into a list of Neo objects to be stored in the db
            val processedResponse: List<Neo> = processNeoResponse(networkResponse.items)
            neoDao.insertAll(processedResponse) // insert them into the db
            success = true
        } catch (e: Exception) {
            TODO("handle the network erroring out gracefully")
        }

        requestInProgress = false
        return success
    }

    // TODO test this method
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