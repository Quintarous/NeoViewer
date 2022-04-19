package com.austin.neoviewer.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.R
import com.austin.neoviewer.databinding.ItemErrorBinding
import com.austin.neoviewer.databinding.ItemNeoBinding

class BrowseRecyclerAdapter(
    private val retryLambda: () -> Unit,
    private val copyLambda: (String) -> Unit
) : ListAdapter<BrowseItem, RecyclerView.ViewHolder>(NEO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // when the item is a Neo object return a NeoViewHolder
        return when (viewType) {
            R.layout.item_neo -> {
                val view = ItemNeoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NeoViewHolder(view, copyLambda)
            }

            // else it must be a network error so return an ErrorViewHolder
            else -> {
                val view = ItemErrorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ErrorViewHolder(view, retryLambda)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NeoViewHolder -> {
                val browseItem = getItem(position) as BrowseItem.Holder
                holder.bind(browseItem.neo)
            }

            is ErrorViewHolder -> {
                val browseItem = getItem(position) as BrowseItem.Error
                holder.bind(browseItem.message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BrowseItem.Holder -> R.layout.item_neo
            else -> R.layout.item_error
        }
    }

    // resetting the cardView's visibility so it doesn't carry over when it's recycled
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is NeoViewHolder) {
            holder.resetViewState()
        }
    }

    companion object {
        private val NEO_COMPARATOR = object : DiffUtil.ItemCallback<BrowseItem>() {
            override fun areItemsTheSame(oldItem: BrowseItem, newItem: BrowseItem): Boolean {
                return if (oldItem is BrowseItem.Holder && newItem is BrowseItem.Holder) {
                    oldItem.neo.id == newItem.neo.id
                } else {
                    false
                }
            }

            override fun areContentsTheSame(oldItem: BrowseItem, newItem: BrowseItem): Boolean =
                oldItem == newItem
        }
    }
}