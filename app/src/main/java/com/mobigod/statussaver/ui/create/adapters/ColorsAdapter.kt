package com.mobigod.statussaver.ui.create.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobigod.statussaver.R
import kotlinx.android.synthetic.main.colors_item_layout.view.*

class ColorsAdapter: RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>(){

    private val colorDrawables = getColorList()
    private var context: Context? = null

    var colorChooserInterface: ColorChooser? = null

    private fun getColorList(): List<ColorItem>{
        return mutableListOf<ColorItem>().apply {
            add(ColorItem(R.drawable.round_white, R.color.white))
            add(ColorItem(R.drawable.round_amber, R.color.amber))
            add(ColorItem(R.drawable.round_eggplant, R.color.eggplant))
            add(ColorItem(R.drawable.round_flax, R.color.flax))
            add(ColorItem(R.drawable.round_green, android.R.color.holo_green_dark))
            add(ColorItem(R.drawable.round_lava, R.color.lava))
            add(ColorItem(R.drawable.round_lemon, R.color.lemon))
            add(ColorItem(R.drawable.round_mauve, R.color.mauve))
            add(ColorItem(R.drawable.round_navy, R.color.navy))
            add(ColorItem(R.drawable.round_orchid, R.color.orchid))
            add(ColorItem(R.drawable.round_pumpkin, R.color.pumpkin))
            add(ColorItem(R.drawable.round_sea, R.color.sea))
            add(ColorItem(R.drawable.round_tuscany, R.color.tuscany))
            add(ColorItem(R.drawable.round_ultra, R.color.ultra))
            add(ColorItem(R.drawable.round_red, R.color.red))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.colors_item_layout, parent, false)
        return ColorsViewHolder(view)
    }

    override fun getItemCount() = colorDrawables.size

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        holder.itemView.color_frame.background =
            ContextCompat.getDrawable(context!!,colorDrawables[position].drawable)
    }



    inner class ColorsViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = colorDrawables[adapterPosition]
            colorChooserInterface?.onColorChoosed(item)
        }
    }

    interface ColorChooser {
        fun onColorChoosed(colorItem: ColorItem)
    }
}

class ColorItem(var drawable: Int, var color: Int)