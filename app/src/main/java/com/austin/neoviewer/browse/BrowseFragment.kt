package com.austin.neoviewer.browse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.austin.neoviewer.databinding.FragBrowseBinding
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

        viewModel.neoList.observe(viewLifecycleOwner) {
            Log.i(TAG, "$it")
        }

        return binding.root
    }
}