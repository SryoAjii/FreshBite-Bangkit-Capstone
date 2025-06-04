package com.example.freshbite.view.map

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.freshbite.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Activity) : GoogleMap.InfoWindowAdapter {

    private val window: View = context.layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun renderWindowText(marker: Marker, view: View) {
        val title = marker.title
        val snippet = marker.snippet
        val parts = snippet?.split("|") ?: listOf("", "", "")

        val placeName = view.findViewById<TextView>(R.id.place_name)
        val placeOpenNow = view.findViewById<TextView>(R.id.place_open_now)
        val placeAddress = view.findViewById<TextView>(R.id.place_address)
        val placeImage = view.findViewById<ImageView>(R.id.place_image)

        placeName.text = title
        placeOpenNow.text = parts.getOrNull(0) ?: "Open status unavailable"
        placeAddress.text = parts.getOrNull(1) ?: "Address unavailable"

        val imageUrl = parts.getOrNull(2)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context).load(imageUrl).into(placeImage)
        } else {
            placeImage.setImageResource(R.drawable.fruit_basket_image)
        }
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderWindowText(marker, window)
        return window
    }

    override fun getInfoContents(marker: Marker): View? {
        renderWindowText(marker, window)
        return window
    }
}
