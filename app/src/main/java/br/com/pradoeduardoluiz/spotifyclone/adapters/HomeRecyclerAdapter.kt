package br.com.pradoeduardoluiz.spotifyclone.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.pradoeduardoluiz.spotifyclone.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_category_list_item.view.*

class HomeRecyclerAdapter(
    private val context: Context,
    private val listener: HomeSelectorListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categories = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_category_list_item, parent, false)
        return ViewHolder(layoutInflater, listener)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = categories[position]

        holder as ViewHolder

        holder.bind(category)
    }

    fun setList(list: MutableList<String>) {
        categories.clear()
        categories.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, listener: HomeSelectorListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val textIdCategory: TextView = itemView.category_id
        private val imageCategory: ImageView = itemView.category_icon
        private var homeSelectorListener: HomeSelectorListener = listener

        fun bind(category: String) {
            textIdCategory.text = category
            itemView.setOnClickListener(this)

            var iconResource: Drawable? = null

            when (category) {
                "Music" -> {
                    iconResource =
                        ContextCompat.getDrawable(context, R.drawable.ic_audiotrack_white_24dp)
                }
                "Podcasts" -> {
                    iconResource = ContextCompat.getDrawable(context, R.drawable.ic_mic_white_24dp)
                }
            }

            Glide.with(context)
                .setDefaultRequestOptions(
                    RequestOptions().error(R.drawable.ic_launcher_background)
                )
                .load(iconResource)
                .into(imageCategory)
        }

        override fun onClick(v: View?) {
            homeSelectorListener.onCategorySelected(adapterPosition)
        }

    }

}

interface HomeSelectorListener {
    fun onCategorySelected(position: Int)
}