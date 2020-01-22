package com.mobigod.statussaver.ui.create.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mobigod.statussaver.R
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.data.model.MusicFile
import com.mobigod.statussaver.databinding.MediaItemLayoutBinding
import com.mobigod.statussaver.databinding.SongsItemViewLayoutBinding
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.removeAllItems
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.time.Duration

class SongsAdapter(val fileSystemMrg: FileSystemManager): RecyclerView.Adapter<SongsAdapter.SongsViewHolder>() {

    lateinit var binding: SongsItemViewLayoutBinding
    private var songs: MutableList<MusicFile> = mutableListOf()
    private var context: Context?=null
    var listener: SongsAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        context = parent.context
        binding = SongsItemViewLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return SongsViewHolder(binding.root)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bindViews(position)
        holder.itemView.setOnClickListener {
            listener?.onSongClicked(songs[position])
        }
    }


    fun addSong(song: MusicFile){
        songs.add(song)
        notifyDataSetChanged()
    }

    fun addAllSongs(mSongs: MutableList<MusicFile>) {
        songs.addAll(mSongs)
        notifyDataSetChanged()
    }


    fun getAllSongs() = songs


    fun removeAllSongs() {
        if (songs.isNotEmpty()){
            songs.clear()
        }
        notifyDataSetChanged()
    }





    inner class SongsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        fun bindViews(position: Int) {
            val musicFile = songs[position]

            binding.songsDuration.text = getSongDuration(musicFile.duration)
            binding.songTitle.text = musicFile.displayName
            binding.album.text = musicFile.artist


            fileSystemMrg.getAlbumArtUri(context!!.contentResolver, musicFile.albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        uri ->

                        val options = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .skipMemoryCache(true)

                        val glide = Glide.with(context!!)
                            .asBitmap()

                        glide.load(uri)
                            .apply(options)
                            .thumbnail(0.5f)
                            .transition(BitmapTransitionOptions.withCrossFade())
                            .into(binding.songAlbumArtMv)

                    },

                    onError = {
                        it.printStackTrace()
                    }
                )
        }

        private fun getSongDuration(duration: Long)
                = Tools.convertMillisecsToReadable(duration)

    }

    interface SongsAdapterListener{
        fun onSongClicked(musicFile: MusicFile)
    }

}