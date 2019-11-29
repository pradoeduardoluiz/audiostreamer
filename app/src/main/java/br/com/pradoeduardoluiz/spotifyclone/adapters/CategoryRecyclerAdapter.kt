package br.com.pradoeduardoluiz.spotifyclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_artist_list_item.view.*

class CategoryRecyclerAdapter(
    private val context: Context,
    private val listener: CategorySelectorListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var artist = mutableListOf<Artist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_artist_list_item, parent, false)
        return ViewHolder(layoutInflater, listener)
    }

    override fun getItemCount() = artist.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val artist = artist[position]

        holder as ViewHolder

        holder.bind(artist)
    }

    fun setList(list: MutableList<Artist>) {
        artist.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, listener: CategorySelectorListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val title: TextView = itemView.title
        private val image: ImageView = itemView.image
        var categorySelectorListener: CategorySelectorListener = listener

        fun bind(artist: Artist) {
            title.text = artist.title
            itemView.setOnClickListener(this)

            Glide.with(context)
                .setDefaultRequestOptions(
                    RequestOptions().error(R.drawable.ic_launcher_background)
                )
                .load(artist.image)
                .into(image)
        }

        override fun onClick(v: View?) {
            categorySelectorListener.onArtistSelected(adapterPosition)
        }

    }

}

interface CategorySelectorListener {
    fun onArtistSelected(position: Int)
}