package com.austin.neoviewer.browse

import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.databinding.ItemErrorBinding

class ErrorViewHolder(
    private val view: ItemErrorBinding,
    private val retry: () -> Unit
): RecyclerView.ViewHolder(view.root) {

    fun bind(message: String) {
        view.errorMessage.text = message
        view.retryButton.setOnClickListener { retry() }
    }
}