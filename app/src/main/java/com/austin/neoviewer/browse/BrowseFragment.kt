package com.austin.neoviewer.browse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.R
import com.austin.neoviewer.databinding.FragBrowseBinding
import com.austin.neoviewer.repository.BrowseResult
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BrowseFragment"

//TODO implement an animated transition for the diameter data card being displayed

@AndroidEntryPoint
class BrowseFragment: Fragment() {

    private val viewModel: BrowseViewModel by viewModels()

    private lateinit var binding: FragBrowseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragBrowseBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        // setting up the browse recycler view
        val adapter = BrowseRecyclerAdapter()
        binding.browseRecycler.adapter = adapter
        val divider = DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL)
        binding.browseRecycler.addItemDecoration(divider)


        // observer for the neoList livedata
        viewModel.neoList.observe(viewLifecycleOwner) { result ->
            // when result is a success process into a list of BrowseItems and submit
            when (result) {
                is BrowseResult.Success -> {
                    val listToSubmit = mutableListOf<BrowseItem>()
                    for (neo in result.items) {
                        listToSubmit.add(BrowseItem.Holder(neo))
                    }
                    adapter.submitList(listToSubmit)
                }
// TODO add a retry button to the error footer
// TODO have some logic for removing the error footer
                // when a network error occurs add a network error footer to the list
                is BrowseResult.Error -> {
                    adapter.apply {
                        if (currentList.isEmpty() || currentList.last() !is BrowseItem.Error) {
                            val outputList = mutableListOf<BrowseItem>()
                            outputList.addAll(currentList)
                            outputList.add(
                                BrowseItem.Error(result.e.message ?: getString(R.string.no_exception))
                            )
                            submitList(outputList)
                        }
                    }
                }
            }
        }

        // adding a scroll listener that requests more data to be loaded when close to the end
        binding.browseRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.browseRecycler.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem + FETCH_DATA_THRESHOLD >= layoutManager.itemCount) {
                    viewModel.getMoreBrowseData() // request more data
                }
            }
        })

        return binding.root
    }

}

private const val FETCH_DATA_THRESHOLD = 8
