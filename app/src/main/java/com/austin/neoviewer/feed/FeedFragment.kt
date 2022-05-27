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

            // if the date range is larger than 1 week throw away the users input
            // this is because the api restricts requests to 1 week or less
            if ((it.second - it.first) > 604800000) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.date_range_too_long_toast),
                    Toast.LENGTH_LONG
                ).show()
                return@addOnPositiveButtonClickListener
            }

            // reporting the users selection to the ViewModel
            val correctedStartMilliseconds = it.first + 86400000
            val correctedEndMilliseconds = it.second + 86400000
            viewModel.submitUiAction(
                FeedViewModel.UiAction(kotlin.Pair(correctedStartMilliseconds, correctedEndMilliseconds))
            )
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
            viewModel.combinedFeedResultFlow.collectLatest { state ->
                adapter.dataset.clear() // clearing the ui for the new data

                // formatting the times into a string to be displayed
                if (state.datePair != null) {
                    val timeString =
                        DateUtils.formatDateTime(
                            requireContext(),
                            state.datePair.first,
                            DateUtils.FORMAT_SHOW_DATE
                        ) + " - " +
                        DateUtils.formatDateTime(
                            requireContext(),
                            state.datePair.second,
                            DateUtils.FORMAT_SHOW_DATE
                        )

                    binding.selectDateRangeLabel.text = timeString
                } else { // if null keep the label as the select prompt
                    binding.selectDateRangeLabel.text = getString(R.string.Select_date_range)
                }

                when(state.feedResult) {
                    is FeedResult.Success -> { // on success update the UI
                        adapter.dataset.addAll(state.feedResult.items)
                    }

                    is FeedResult.Error -> { // on error show a toast
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.network_error_toast),
                            Toast.LENGTH_SHORT
                        ).show()
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
}