package com.mobigod.statussaver.ui.split.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobigod.statussaver.databinding.FolderItemViewLayoutBinding
import java.io.File

class FoldersAdapter: RecyclerView.Adapter<FoldersAdapter.FoldersViewHolder>() {
    private val files: MutableList<File> = mutableListOf()

    lateinit var binding: FolderItemViewLayoutBinding

    var folderListener: FolderListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolder {
        binding = FolderItemViewLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return FoldersViewHolder(binding.root)
    }


    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FoldersViewHolder, position: Int) {
        holder.bindView(position)
    }


    fun addAllFiles(fileList: MutableList<File>) {
        files.addAll(fileList)
        notifyDataSetChanged()
    }

    fun removeAllFiles() {
        files.clear()
    }


    inner class FoldersViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindView(position: Int) {
            val file = files[position]
            binding.folderNameTv.text = file.name
            binding.numOfVideo.text = "${file.listFiles().size} videos"

            binding.folderItem.setOnClickListener {
                folderListener?.onClicked(file.absolutePath)
            }

        }
    }


    interface FolderListener {
        fun onClicked(file: String)
    }

}