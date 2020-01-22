package com.mobigod.statussaver.ui.split.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mobigod.statussaver.databinding.MediaItemLayoutBinding
import com.mobigod.statussaver.databinding.VideoItemLayoutBinding
import java.io.File

class VideoAdapter(val context: Context): RecyclerView.Adapter<VideoAdapter.VideoGridViewHolder>(){
    private val mVideos = mutableListOf<File>()

    lateinit var binding: VideoItemLayoutBinding
    var listener: VideoListener? = null

    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .skipMemoryCache(true)


    val glide = Glide.with(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoGridViewHolder {
        binding = VideoItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return VideoGridViewHolder(binding.root)
    }

    override fun getItemCount(): Int = mVideos.size

    override fun onBindViewHolder(holder: VideoGridViewHolder, position: Int) {
        holder.bindViewAt(position)
    }


    fun addAllVideo(videos: MutableList<File>) {
        mVideos.addAll(videos)
        notifyDataSetChanged()
    }



    inner class VideoGridViewHolder(item: View): RecyclerView.ViewHolder(item) {

        fun bindViewAt(position: Int) {
            val file = mVideos[position]
            binding.folderNameTv.text = file.name

            binding.rRelative.setOnClickListener {
                listener?.onVideoClicked(file.absolutePath)
            }

            glide.load(file)
                .apply(options)
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.thumbnail)
        }

    }

    interface VideoListener {
        fun onVideoClicked(file: String)
    }
}