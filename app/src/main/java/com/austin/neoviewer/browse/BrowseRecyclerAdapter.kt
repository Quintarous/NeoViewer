package com.austin.neoviewer.browse

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.austin.neoviewer.R
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.databinding.ItemNeoBinding

class BrowseRecyclerAdapter: ListAdapter<Neo, NeoViewHolder>(NEO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeoViewHolder {
        val view = ItemNeoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NeoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NeoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val NEO_COMPARATOR = object : DiffUtil.ItemCallback<Neo>() {
            override fun areItemsTheSame(oldItem: Neo, newItem: Neo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Neo, newItem: Neo): Boolean =
                oldItem == newItem
        }
    }
}