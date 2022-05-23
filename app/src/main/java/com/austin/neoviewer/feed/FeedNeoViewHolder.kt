package com.austin.neoviewer.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.database.FeedNeo
import com.austin.neoviewer.databinding.ItemFeedNeoBinding

class FeedNeoViewHolder(
    private val binding: ItemFeedNeoBinding,
    private val copyLambda: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            binding.cardView.toggleVisibility()
        }
        binding.jplCopyButton.setOnClickListener { copyLambda.invoke(binding.neoJplUrl.text.toString()) }
    }

    private fun View.toggleVisibility() {
        visibility = when (visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun resetViewState() {
        binding.cardView.visibility = View.GONE
    }

    fun bind(neo: FeedNeo) {
        // basic data
        binding.neoName.text = neo.name
        binding.neoJplUrl.text = neo.jplUrl
        binding.neoHazardous.text = neo.hazardous.toString()

        // diameter data
        // kilometers
        binding.neoKilometersMin.text = neo.kilometersDiamMin.toString()
        binding.neoKilometersMax.text = neo.kilometersDiamMax.toString()

        // meters
        binding.neoMetersMin.text = neo.metersDiamMin.toString()
        binding.neoMetersMax.text = neo.metersDiamMax.toString()

        // miles
        binding.neoMilesMin.text = neo.milesDiamMin.toString()
        binding.neoMilesMax.text = neo.milesDiamMax.toString()

        // feet
        binding.neoFeetMin.text = neo.feetDiamMin.toString()
        binding.neoFeetMax.text = neo.feetDiamMax.toString()
    }
}