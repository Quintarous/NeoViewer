package com.austin.neoviewer.feed

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.austin.neoviewer.R
import com.austin.neoviewer.databinding.FragFeedBinding
import com.austin.neoviewer.repository.FeedResult
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "FeedFragment"
// TODO double clicking the date selector crashes
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
                    MaterialDatePicker.todayInUtcMilliseconds().minus(604800000),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .setCalendarConstraints(calendarConstraints)
            .build()


        // requesting data and updating the UI when the user confirms a new date range selection
        dateRangePicker.addOnPositiveButtonClickListener {
            // requesting data from the backend
            val firstDate = Calendar.getInstance().apply { timeInMillis = it.first }
            val secondDate = Calendar.getInstance().apply { timeInMillis = it.second }

            if ((secondDate.timeInMillis - firstDate.timeInMillis) > 604800000) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.date_range_too_long_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return@addOnPositiveButtonClickListener
            }

            binding.loadingBar.visibility = View.VISIBLE // showing the loading bar

            viewModel.requestNewData(firstDate.formatDateForApi(), secondDate.formatDateForApi())

            // updating the UI with the users selection
            // 1 day in milliseconds is added to the times because DateUtils displays one day
            // behind what the date picker shows
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


        // creating the copy lambda the recycler view will use to copy the jpl url
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
        viewModel.feedLiveData.observe(viewLifecycleOwner) { feedResult ->
            Log.i(TAG, "$feedResult")
            binding.loadingBar.visibility = View.GONE // turning off the loading spinner
            adapter.dataset.clear() // clearing the ui for the new data
            when (feedResult) {
                is FeedResult.Success -> {
                    adapter.dataset.addAll(feedResult.items)
                }

                is FeedResult.Error -> {
                    Toast.makeText(requireContext(), feedResult.e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
            adapter.notifyDataSetChanged()
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