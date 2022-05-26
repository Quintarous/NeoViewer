package com.austin.neoviewer.feed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.austin.neoviewer.R
import com.austin.neoviewer.databinding.FragFeedBinding
import com.austin.neoviewer.repository.FeedResult
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "FeedFragment"

@AndroidEntryPoint
class FeedFragment: Fragment() {

    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragFeedBinding.inflate(inflater, container, false)

        val savedState = savedStateRegistry
        // TODO implement shared preferences

        // building the date picker dialog
        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.Select_date_range))
            .setSelection(
                Pair(
                    // setting the default selection to be 7 days ago to today
                    MaterialDatePicker.todayInUtcMilliseconds().minus(604800000),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .setCalendarConstraints(calendarConstraints)
            .build()


        // requesting data and updating the UI when the user confirms a new date range selection
        dateRangePicker.addOnPositiveButtonClickListener {
            // requesting data from the backend
            // these calendars have 1 day added to them because the date picker returns the
            // millisecond time for the day before the users selection
            val firstDate = Calendar.getInstance().apply {
                timeInMillis = it.first
                set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
            }
            val secondDate = Calendar.getInstance().apply {
                timeInMillis = it.second
                set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
            }

            // if the date range is larger than 1 week throw away the users input
            // this is because the api restricts requests to 1 week or less
            if ((secondDate.timeInMillis - firstDate.timeInMillis) > 604800000) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.date_range_too_long_toast),
                    Toast.LENGTH_LONG
                ).show()
                return@addOnPositiveButtonClickListener
            }

            // requesting the data
            viewModel.requestNewData(firstDate.formatDateForApi(), secondDate.formatDateForApi())

            // updating the UI with the users selection. 1 day in milliseconds is added to the
            // times because DateUtils displays one day behind what the date picker shows
            val timeString =
                DateUtils.formatDateTime(requireContext(), it.first + 86400000, DateUtils.FORMAT_SHOW_DATE) +
                " - " +
                DateUtils.formatDateTime(requireContext(), it.second + 86400000, DateUtils.FORMAT_SHOW_DATE)

            binding.selectDateRangeLabel.text = timeString
        }


        // show the range picker when the button is pressed
        binding.selectDateRangeLabel.setOnClickListener {
            if (!dateRangePicker.isAdded) {
                dateRangePicker.show(parentFragmentManager, "FeedDatePicker")
            }
        }


        // creating the copy lambda the recycler view will use to copy the jpl url to the system clipboard
        val clipboard = ContextCompat.getSystemService(
            requireContext(),
            ClipboardManager::class.java
        ) as ClipboardManager

        val copyLambda = { url: String ->
            val clip = ClipData.newPlainText("plain text", url)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.copy_toast), Toast.LENGTH_SHORT).show()
        }


        // setting up the recycler view that will display the data
        val adapter = FeedRecyclerAdapter(copyLambda)
        binding.feedRecycler.adapter = adapter


        // updating the UI based on the returned feed data
        lifecycleScope.launch {
            viewModel.combinedFeedResultFlow.collectLatest { feedResult ->
                adapter.dataset.clear() // clearing the ui for the new data
                when (feedResult) {
                    is FeedResult.Success -> {
                        adapter.dataset.addAll(feedResult.items)
                    }

                    is FeedResult.Error -> {
                        Toast.makeText(requireContext(), R.string.network_error_toast, Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }


        // tying the loading bars visibility to the repositories requestInProgress flow
        viewModel.isRequestInProgress.observe(viewLifecycleOwner) {
            binding.loadingBar.isVisible = it
        }

        return binding.root
    }


    /**
     * The NeoWs Nasa API requires the start and end date in a yyyy-mm-dd format. This function
     * takes a Calendar object and generates an appropriate string from it.
     */
    private fun Calendar.formatDateForApi(): String {
        val year = get(Calendar.YEAR)
        val month = get(Calendar.MONTH)
        val day = get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }
}