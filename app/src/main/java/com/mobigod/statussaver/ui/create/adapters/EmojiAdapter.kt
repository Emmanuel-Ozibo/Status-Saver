package com.mobigod.statussaver.ui.create.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.mobigod.statussaver.R
import com.mobigod.statussaver.databinding.EmojisItemViewBinding

class EmojiAdapter: RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>(){
    lateinit var binding: EmojisItemViewBinding
    private var context: Context? = null
    lateinit var emojiListener: EmojiListener

    private val listOfEmoji: MutableList<EmojiItem> = mutableListOf(
        EmojiItem(R.drawable.ic_angry, "Angry"),
        EmojiItem(R.drawable.ic_angry_1, "Angry01"),
        EmojiItem(R.drawable.ic_bored, "Bored"),
        EmojiItem(R.drawable.ic_bored_1, "Bored01"),
        EmojiItem(R.drawable.ic_confused, "confused"),
        EmojiItem(R.drawable.ic_crying, "crying"),
        EmojiItem(R.drawable.ic_embarrassed, "embarrassed"),
        EmojiItem(R.drawable.ic_happy_4, "happy"),
        EmojiItem(R.drawable.ic_in_love, "In_love"),
        EmojiItem(R.drawable.ic_kissing, "kissing"),
        EmojiItem(R.drawable.ic_mad, "Mad"),
        EmojiItem(R.drawable.ic_nerd, "Nerd"),
        EmojiItem(R.drawable.ic_quiet, "quiet"),
        EmojiItem(R.drawable.ic_sad, "sad"),
        EmojiItem(R.drawable.ic_smile, "Smile"),
        EmojiItem(R.drawable.ic_suspicious, "suspicious"),
        EmojiItem(R.drawable.ic_tongue_out_1, "Tongue_out"),
        EmojiItem(R.drawable.ic_wink, "Wink"))



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        context = parent.context
        binding = EmojisItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return EmojiViewHolder(binding.root)
    }

    override fun getItemCount() = listOfEmoji.size

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bindView(position)
    }


    interface EmojiListener {
        fun onEmojiSelected(emojiItem: EmojiItem)
    }

    inner class EmojiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        init {
            binding.emojiMv.setOnClickListener {
                emojiListener.onEmojiSelected(listOfEmoji[adapterPosition])
            }
        }

        fun bindView(position: Int) {
            val item = listOfEmoji[position]
            binding.emojiMv.setImageDrawable(ContextCompat.getDrawable(context!!, item.emoji))

        }
    }
}

class EmojiItem (
    var emoji: Int = 0,
    var name: String = ""
)