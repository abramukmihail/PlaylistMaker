package com.example.playlistmaker.presentation.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        val millis = track.trackTimeMillis.toLongOrNull() ?: 0L
        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        tvArtistName.requestLayout()
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(2))
            .override(45, 45)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .apply(requestOptions)
            .placeholder(R.drawable.ic_placeholder_45)
            .error(R.drawable.ic_placeholder_45)
            .into(ivArtwork)
    }
}