package com.austin.neoviewer.feed

import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.databinding.ItemSeparatorBinding

class SeparatorViewHolder(private val binding: ItemSeparatorBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.separatorText.text = text
    }
}