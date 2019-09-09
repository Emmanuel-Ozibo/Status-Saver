package com.mobigod.statussaver.ui.saver.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobigod.statussaver.data.model.MediaItemModel
import com.mobigod.statussaver.databinding.MediaItemLayoutBinding
import com.mobigod.statussaver.global.hide
import com.mobigod.statussaver.global.removeAllItems
import com.bumptech.glide.request.RequestOptions


class MediaFilesAdapter(val options: RequestOptions, val glide: RequestBuilder<Bitmap>, val glideMain: RequestManager,
                        val onitemClick: (MediaItemModel, List<MediaItemModel>, position: Int) -> Unit,
                        val onItem: (View) -> Unit):
                        RecyclerView.Adapter<MediaFilesAdapter.MediaFilesViewHolder>() {
    val mediaItems = mutableListOf<MediaItemModel>()

    lateinit var binding: MediaItemLayoutBinding
    var context: Context? = null
    var itemView: View? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaFilesViewHolder {
        context = parent.context
        binding = MediaItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaFilesViewHolder(binding.root)
    }

    override fun getItemCount() = mediaItems.size

    override fun onBindViewHolder(holder: MediaFilesViewHolder, position: Int){
        holder.itemView.setOnClickListener {
            onitemClick(mediaItems[position], mediaItems, position)
        }

        if (position == 0){
            onItem(holder.itemView)
        }
        holder.bindView(position)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }


    fun addAll(list: MutableList<MediaItemModel>) {
        mediaItems.addAll(list)
        notifyDataSetChanged()
    }

    fun getFirstItem() = itemView

    private fun removeAllItems() {
        mediaItems.removeAllItems()
        notifyDataSetChanged()
    }


    inner class MediaFilesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindView(position: Int) {
            val item = mediaItems[position]
            if (item.type == MediaItemType.IMAGE_MEDIA) {
                binding.playIcon.hide()
            }

            glide.load(item.file)
                .apply(options)
                .thumbnail(0.5f)
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(binding.thumbnail)
        }
    }
}