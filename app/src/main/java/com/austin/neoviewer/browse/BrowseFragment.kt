package com.austin.neoviewer.browse

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.austin.neoviewer.R
import com.austin.neoviewer.databinding.FragBrowseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "BrowseFragment"

lateinit var retryLambda: () -> Unit

@AndroidEntryPoint
class BrowseFragment: Fragment() {

    private val viewModel: BrowseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragBrowseBinding.inflate(inflater, container, false).apply {
            viewModel = viewModel
        }
        setHasOptionsMenu(true)

        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager


        // setting up the browse recycler view with it's adapter
        val copyLambda = { url: String ->
            val clip = ClipData.newPlainText("plain text", url)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.copy_toast), Toast.LENGTH_SHORT).show()
        }
        val adapter = BrowseRecyclerAdapter(copyLambda)
        val header = BrowseLoadStateAdapter { adapter.retry() }
        retryLambda = { adapter.retry() } // this is for the retry menu button
        val concatAdapter = adapter.withLoadStateHeaderAndFooter(
            footer = BrowseLoadStateAdapter { adapter.retry() },
            header = header
        )
        binding.browseRecycler.adapter = concatAdapter

        // adding the divider decorations to make the list more readable
        val divider = DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL)
        binding.browseRecycler.addItemDecoration(divider)


        // initiate a data refresh when the user does a swipe to refresh gesture
        binding.browseSwipeRefresh.setOnRefreshListener {
            adapter.retry()
        }

        // setting the on click listener for the retry button that appears when no data is cached
        binding.browseFragRetryButton.setOnClickListener { adapter.retry() }


        // collecting the pagingData and submitting it to the adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }


        // Syncing up the UI to the current LoadState
        viewLifecycleOwner.lifecycleScope.launch {
            val swipeRefresh = binding.browseSwipeRefresh
            adapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) {
                    is LoadState.NotLoading -> {
                        if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
                    }

                    is LoadState.Error -> {
                        if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
                    }
                }

                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0}
                    ?: loadState.prepend

                binding.browseFragProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                binding.browseFragRetryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
                binding.browseFragErrorMessage.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.network_error_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }


    // setting up the menu with the refresh button
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.browse_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                retryLambda.invoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
