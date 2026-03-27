package com.example.playlistmaker.search.ui.activity.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Context
import android.util.TypedValue

class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onTrackClick(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        this.tracks = newTracks
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.tvTrackName.text = track.trackName
            binding.tvArtistName.text = track.artistName

            val millis = track.trackTimeMillis.toLongOrNull() ?: 0L
            binding.tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)

            binding.tvArtistName.requestLayout()
            val cornerRadiusInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f, // 2dp
                context.resources.displayMetrics
            ).toInt()

            val requestOptions = RequestOptions()
                .transform(RoundedCorners(cornerRadiusInPx))
                .override(45, 45)

            Glide.with(binding.root)
                .load(track.artworkUrl100)
                .apply(requestOptions)
                .placeholder(R.drawable.ic_placeholder_45)
                .error(R.drawable.ic_placeholder_45)
                .into(binding.ivArtwork)
        }
    }
}