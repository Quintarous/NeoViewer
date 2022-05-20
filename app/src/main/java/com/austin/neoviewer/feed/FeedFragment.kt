package com.austin.neoviewer.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.austin.neoviewer.databinding.FragFeedBinding
import dagger.hilt.android.AndroidEntryPoint

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

// TODO setup the UI to display the data we get from this flow and determine how we're going to communicate user input
        viewModel.feedFlow.observe(viewLifecycleOwner) {
            Log.i(TAG, "$it")
        }
        binding.testButton.setOnClickListener {
            viewModel.requestNewData("2015-07-08", "2015-07-09")
        }

        return binding.root
    }
}