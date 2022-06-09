package com.austin.neoviewer.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.R
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.databinding.ItemFeedNeoBinding
import com.austin.neoviewer.databinding.ItemSeparatorBinding
import com.austin.neoviewer.repository.FeedResultItem

class FeedRecyclerAdapter(private val copyLambda: (String) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataset = mutableListOf<FeedResultItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.item_separator -> {
                val binding = ItemSeparatorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SeparatorViewHolder(binding)
            }

            else -> {
                val binding = ItemFeedNeoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FeedNeoViewHolder(binding, copyLambda)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SeparatorViewHolder -> {
                val item = dataset[position] as FeedResultItem.Separator
                holder.bind(item.date)
            }

            is FeedNeoViewHolder -> {
                val item = dataset[position] as FeedResultItem.Neo
                holder.bind(item.neo)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is FeedNeoViewHolder) holder.resetViewState()
    }

    override fun getItemViewType(position: Int): Int =
        when (dataset[position]) {
            is FeedResultItem.Separator -> R.layout.item_separator
            else -> R.layout.item_feed_neo
        }

    override fun getItemCount(): Int = dataset.size
}