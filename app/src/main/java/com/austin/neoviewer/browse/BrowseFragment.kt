package com.austin.neoviewer.browse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
            viewmodel = viewModel
        }

        val adapter = BrowseRecyclerAdapter()
        binding.browseRecycler.adapter = adapter

        // observer on the neoList livedata list of Neo objects
        viewModel.neoList.observe(viewLifecycleOwner) { result ->
            when (result) {
                // on a successful network response submit the list to the recycler view
                is BrowseResult.Success -> adapter.submitList(result.items)
                // on a failed network response display the error message in a toast
                is BrowseResult.Error -> Toast.makeText(
                    this.requireContext(),
                    result.e.message,
                    Toast.LENGTH_LONG
                ).show()
                // TODO ditch the toast and replace it with a footer
            }
        }

        return binding.root
    }
}