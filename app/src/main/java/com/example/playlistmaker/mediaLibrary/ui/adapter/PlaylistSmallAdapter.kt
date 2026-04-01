package com.example.playlistmaker.mediaLibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistSmallBinding
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist

class PlaylistSmallAdapter(
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistSmallAdapter.PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistSmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick(playlists[position])
        }
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistSmallBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName2.text = playlist.name

            val trackCountText = itemView.context.resources.getQuantityString(
                R.plurals.track_count,
                playlist.trackCount,
                playlist.trackCount
            )
            binding.playlistTrackCount2.text = trackCountText

            val cornerRadius = itemView.context.resources.getDimensionPixelSize(R.dimen.small_cover_radius)

            Glide.with(itemView)
                .load(playlist.coverPath)
                .placeholder(R.drawable.ic_placeholder_45)
                .error(R.drawable.ic_placeholder_45)
                .transform(RoundedCorners(cornerRadius))
                .into(binding.ivCover)
        }
    }
}