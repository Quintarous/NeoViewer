package com.austin.neoviewer.browse

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.austin.neoviewer.databinding.ItemErrorBinding

class BrowseLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<BrowseLoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BrowseLoadStateViewHolder {
        val binding = ItemErrorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrowseLoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: BrowseLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

}