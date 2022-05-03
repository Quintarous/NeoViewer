package com.austin.neoviewer.browse

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
            viewModel = viewModel
        }

        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager


        // setting up the browse recycler view with it's adapter
        val retryLambda = { viewModel.retry(binding.browseSwipeRefresh) }
        val copyLambda = { url: String ->
            val clip = ClipData.newPlainText("plain text", url)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied Url", Toast.LENGTH_SHORT).show()
        }
        val adapter = BrowseRecyclerAdapter(retryLambda, copyLambda)
        binding.browseRecycler.adapter = adapter
        val divider = DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL)
        binding.browseRecycler.addItemDecoration(divider)


        binding.browseSwipeRefresh.setOnRefreshListener {
            viewModel.retry(binding.browseSwipeRefresh)
        }


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

                // when a network error occurs add a network error footer to the end of the list
                is BrowseResult.Error -> {
                    adapter.apply {
                        if (currentList.isEmpty() || currentList.last() !is BrowseItem.Error) {
                            Toast.makeText(
                                context,
                                getString(R.string.network_error_toast),
                                Toast.LENGTH_SHORT
                            ).show() // show a toast alerting the user a network error occurred

                            // showing the network error item and retry button as a footer
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.browse_fragment_menu, menu)
        // TODO this menu doesn't show up
    }

}

private const val FETCH_DATA_THRESHOLD = 8
