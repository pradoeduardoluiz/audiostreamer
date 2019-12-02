package br.com.pradoeduardoluiz.spotifyclone.adapters

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.pradoeduardoluiz.spotifyclone.R
import kotlinx.android.synthetic.main.layout_playlist_list_item.view.*

class PlaylistRecyclerAdapter(
    private val context: Context,
    private val listener: MediaSelectorListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mediaList = mutableListOf<MediaMetadataCompat>()
    private var selectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_playlist_list_item, parent, false)
        return ViewHolder(layoutInflater, listener)
    }

    override fun getItemCount() = mediaList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaItem = mediaList[position]

        holder as ViewHolder

        holder.bind(mediaItem, selectedIndex, position)
    }

    fun setList(list: MutableList<MediaMetadataCompat>) {
        mediaList.addAll(list)
        notifyDataSetChanged()
    }

    fun setSelectedIndex(index: Int) {
        selectedIndex = index
        notifyDataSetChanged()
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }


    fun getIndexOfItem(mediaItem: MediaMetadataCompat): Int {
        for ((index, it) in mediaList.withIndex()) {
            if (it.description.mediaId.equals(mediaItem.description.mediaId)) {
                return index
            }
        }
        return -1
    }

    inner class ViewHolder(itemView: View, listener: MediaSelectorListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val title: TextView = itemView.media_song_title
        private val artist: TextView = itemView.media_artist
        var mediaSelectorListener: MediaSelectorListener = listener

        fun bind(
            mediaItem: MediaMetadataCompat,
            selectedIndex: Int,
            position: Int
        ) {
            title.text = mediaItem.description.title
            artist.text = mediaItem.description.subtitle

            if (position == selectedIndex) {
                title.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                title.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        override fun onClick(v: View?) {
            mediaSelectorListener.onMediaSelected(adapterPosition)
        }

    }

}

interface MediaSelectorListener {
    fun onMediaSelected(position: Int)
}