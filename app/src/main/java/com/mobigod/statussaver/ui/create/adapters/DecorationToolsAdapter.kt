package com.mobigod.statussaver.ui.create.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobigod.statussaver.R
import com.mobigod.statussaver.databinding.DecoToolsItemLayoutBinding

class DecorationToolsAdapter(val onItemClicked: (DecorationToolsItem, View) -> Unit): RecyclerView.Adapter<DecorationToolsAdapter.DecorationToolsVH>(){
    lateinit var binding: DecoToolsItemLayoutBinding
    val decoTools = mutableListOf<DecorationToolsItem>().apply {
        add(DecorationToolsItem(R.drawable.ic_palette, "Background"))
        add(DecorationToolsItem(R.drawable.ic_audio, "Audio"))
        add(DecorationToolsItem(R.drawable.ic_emoji, "Emoji"))
        add(DecorationToolsItem(R.drawable.ic_brush, "Brush"))
        add(DecorationToolsItem(R.drawable.ic_picture, "Picture"))
        add(DecorationToolsItem(R.drawable.ic_eraser, "Eraser"))
        add(DecorationToolsItem(R.drawable.ic_edit, "Text"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorationToolsVH {
        binding = DecoToolsItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return DecorationToolsVH(binding.root)
    }

    override fun getItemCount() = decoTools.size

    override fun onBindViewHolder(holder: DecorationToolsVH, position: Int) {
        holder.bindTo(position)
        holder.itemView.setOnClickListener {
            onItemClicked(decoTools[position], holder.itemView)
        }
    }


    inner class DecorationToolsVH(item: View): RecyclerView.ViewHolder(item) {
        fun bindTo(position: Int) {
            binding.toolImg.setImageDrawable(itemView.context.getDrawable(decoTools[position].imageRes))
            binding.toolTitle.text = decoTools[position].title
        }
    }

}

class DecorationToolsItem(
    var imageRes: Int,
    var title: String
)