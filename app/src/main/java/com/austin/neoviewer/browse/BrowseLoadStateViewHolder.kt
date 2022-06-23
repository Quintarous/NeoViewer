package com.austin.neoviewer.browse

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.databinding.ItemErrorBinding

class BrowseLoadStateViewHolder(
    private val binding: ItemErrorBinding,
    retry: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMessage.text = loadState.error.localizedMessage
        }

        binding.errorLabel.isVisible = loadState is LoadState.Error
        binding.errorMessage.isVisible = loadState is LoadState.Error
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.progressBar.isVisible = loadState is LoadState.Loading
    }
}