package com.austin.neoviewer.feed

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.austin.neoviewer.repository.FeedResult
import com.austin.neoviewer.repository.NeoRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor (
    private val repository: NeoRepositoryInterface,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    /**
     * When the user selects a date range it is cached in the [standbyDateRange] variable. If the
     * network request ends up being successful than the date range will be cached in
     * [SharedPreferences] and [currentDateRange] will also be updated.
     *
     * This is to keep the displayed date range in the UI in sync with the actual data from the db.
     */
    private var standbyDateRange: Pair<Long, Long>? = null
    private var currentDateRange: Pair<Long, Long>? = null

    // collecting load status flow and exposing it to the UI
    val isRequestInProgress = liveData(dispatcher) {
        emitSource(repository.requestInProgress.asLiveData())
    }

    // this is the flow that is exposed to the UI
    private val _combinedFeedResultFlow = MutableStateFlow<UiState>(
        UiState(null, FeedResult.Success(listOf()))
    )
    val combinedFeedResultFlow: StateFlow<UiState>
        get() = _combinedFeedResultFlow


    init {
        // on startup attempt to pull the date query from the SharedPreferences
        val cachedFirstTime = sharedPreferences.getLong(START_KEY, 0L)
        val cachedSecondTime = sharedPreferences.getLong(END_KEY, 0L)
        if (cachedFirstTime != 0L && cachedSecondTime != 0L) {
            currentDateRange = Pair(cachedFirstTime, cachedSecondTime)
        }

        // collecting from the error flow and data flow and combining them into one FeedResult flow
        // for the UI
        viewModelScope.launch(dispatcher) {
            repository.getFeedFlow().collectLatest {
                // reporting to the repository that the request finished and was received
                if (repository.requestInProgress.value) {
                    repository.requestInProgress.emit(false)
                }

                // moving the up to date standby date range value into the current variable
                if (standbyDateRange != null) currentDateRange = standbyDateRange

                // assuming it's not null cache the currentDateRange in SharedPreferences
                if (currentDateRange != null) {
                    sharedPreferences.edit()
                        .putLong(START_KEY, currentDateRange!!.first)
                        .putLong(END_KEY, currentDateRange!!.second)
                        .apply()
                }

                _combinedFeedResultFlow.emit(UiState(
                    datePair = currentDateRange,
                    feedResult = FeedResult.Success(it)
                ))
            }
        }

        viewModelScope.launch(dispatcher) {
            repository.getErrorFlow().collectLatest {
                if (repository.requestInProgress.value) {
                    repository.requestInProgress.emit(false)
                }

                _combinedFeedResultFlow.emit(UiState(
                    datePair = currentDateRange,
                    feedResult = it
                ))
            }
        }
    }


    /**
     * submitUiAction is exposed to the UI so it can submit [UiAction] objects to the view model.
     */
    fun submitUiAction(action: UiAction) {
        // grabbing the first and second time values from the UiAction
        val first = action.timeRange.first
        val second = action.timeRange.second


        // caching the date range in the standby variable to be more permanently cached if the request succeeds
        standbyDateRange = Pair(first, second)

        // converting the times to calendar objects
        val firstTime = Calendar.getInstance().apply {
            timeInMillis = action.timeRange.first
        }
        val secondTime = Calendar.getInstance().apply {
            timeInMillis = action.timeRange.second
        }

        // making the API request with the correct date format
        Log.i("bruh", "firstTime: ${firstTime.formatDateForApi()}")
        Log.i("bruh", "secondTime: ${secondTime.formatDateForApi()}")
        requestNewData(firstTime.formatDateForApi(), secondTime.formatDateForApi())
    }


    // asks the repository to make a network request for the following dates and cache it in the db
    private fun requestNewData(start: String, end: String) {
        viewModelScope.launch(dispatcher) {
            repository.getNewFeedData(start, end)
        }
    }


    companion object {
        const val PREFERENCES_KEY = "feed_shared_preferences"
        const val START_KEY = "start_date_key"
        const val END_KEY = "end_date_key"
    }


    /**
     * UiState contains the start and end date Long values so the FeedViewModel has a way to tell
     * the UI what date to display
     *
     * And of course there's the FeedResult which contains the actual data to be displayed.
     */
    data class UiState(
        val datePair: Pair<Long, Long>?,
        val feedResult: FeedResult
    )

    /**
     * UiAction is a data class that encapsulates data produced by the UI controller so it can be
     * passed to the ViewModel via a single method. And if any new things need to be reported in
     * the future they can simply be added to this UiAction class.
     */
    data class UiAction(
        val timeRange: Pair<Long, Long>
    )


    /**
     * The NeoWs Nasa API requires the start and end date in a yyyy-mm-dd format. This function
     * takes a Calendar object and generates an appropriate string from it.
     */
    private fun Calendar.formatDateForApi(): String {
        val year = get(Calendar.YEAR)
        val month = get(Calendar.MONTH).plus(1)
        val day = get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }
}