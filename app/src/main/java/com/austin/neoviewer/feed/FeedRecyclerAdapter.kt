package com.austin.neoviewer.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.databinding.ItemFeedNeoBinding

class FeedRecyclerAdapter(private val copyLambda: (String) -> Unit)
    : RecyclerView.Adapter<FeedNeoViewHolder>() {

    val dataset = mutableListOf<FeedNeo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedNeoViewHolder {
        val binding = ItemFeedNeoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedNeoViewHolder(binding, copyLambda)
    }

    override fun onBindViewHolder(holder: FeedNeoViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun onViewRecycled(holder: FeedNeoViewHolder) {
        holder.resetViewState()
    }

    override fun getItemCount(): Int = dataset.size
}