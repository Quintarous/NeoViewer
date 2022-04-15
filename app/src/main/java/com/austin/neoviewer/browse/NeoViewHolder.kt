package com.austin.neoviewer.browse

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.austin.neoviewer.database.Neo
import com.austin.neoviewer.databinding.ItemNeoBinding

class NeoViewHolder (private val view: ItemNeoBinding): RecyclerView.ViewHolder(view.root) {

    init {
        view.root.setOnClickListener {
            view.cardView.toggleVisibility()
        }
    }

    private fun View.toggleVisibility() {
        visibility = when (visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun bind(neo: Neo) {
        view.neoName.text = neo.name
        view.neoDesignation.text = neo.designation
        view.neoJplUrl.text = neo.jplUrl
        view.neoHazardous.text = neo.hazardous.toString()

        view.neoKilometersMin.text = neo.kilometersDiamMin.toString()
        view.neoKilometersMax.text = neo.kilometersDiamMax.toString()

        view.neoMetersMin.text = neo.metersDiamMin.toString()
        view.neoMetersMax.text = neo.metersDiamMax.toString()

        view.neoMilesMin.text = neo.milesDiamMin.toString()
        view.neoMilesMax.text = neo.milesDiamMax.toString()

        view.neoFeetMin.text = neo.feetDiamMin.toString()
        view.neoFeetMax.text = neo.feetDiamMax.toString()
    }
}