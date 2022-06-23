package com.austin.neoviewer.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.databinding.ItemNeoBinding

class BrowseRecyclerAdapter(
    private val copyLambda: (String) -> Unit
) : PagingDataAdapter<Neo, NeoViewHolder>(NEO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeoViewHolder {
        val view = ItemNeoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NeoViewHolder(view, copyLambda)
    }

    override fun onBindViewHolder(holder: NeoViewHolder, position: Int) {
        holder.bind(getItem(position)
            ?: throw UnsupportedOperationException("BrowseRecyclerAdapter.onBindViewHolder: getItem(position) is null"))
    }

    // resetting the cardView's visibility so it doesn't carry over when it's recycled
    override fun onViewRecycled(holder: NeoViewHolder) {
        holder.resetViewState()
    }

    companion object {
        private val NEO_COMPARATOR = object : DiffUtil.ItemCallback<Neo>() {
            override fun areItemsTheSame(oldItem: Neo, newItem: Neo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Neo, newItem: Neo): Boolean =
                oldItem == newItem
        }
    }
}