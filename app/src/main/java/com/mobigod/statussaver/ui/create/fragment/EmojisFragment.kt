package com.mobigod.statussaver.ui.create.fragment

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.elyeproj.drawtext.dpToPx
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.databinding.FragmentEmojisLayoutBinding
import com.mobigod.statussaver.ui.create.adapters.EmojiAdapter
import com.mobigod.statussaver.ui.create.adapters.EmojiItem
import com.mobigod.statussaver.ui.saver.adapter.decos.SpacesItemDecoration
import kotlin.ClassCastException

class EmojisFragment: BaseFragment<FragmentEmojisLayoutBinding>(){
    lateinit var binding: FragmentEmojisLayoutBinding
    private val emojiAdapter = EmojiAdapter()
    lateinit var listener: EmojiFragmentListener

    override fun getLayoutRes() = R.layout.fragment_emojis_layout


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EmojiFragmentListener
        }catch (e: ClassCastException){
            throw ClassCastException("Shit........")
        }
    }


    override fun initComponents() {
        binding = getBinding()
        emojiAdapter.emojiListener = object : EmojiAdapter.EmojiListener {
            override fun onEmojiSelected(emojiItem: EmojiItem) {
                listener.onEmojiSelected(emojiItem)
            }

        }


        binding.emojiRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
           // addItemDecoration(SpacesItemDecoration(resources.dpToPx(5)))
            adapter = emojiAdapter
        }


    }

    interface EmojiFragmentListener {
        fun onEmojiSelected(emojiItem: EmojiItem)
    }

}