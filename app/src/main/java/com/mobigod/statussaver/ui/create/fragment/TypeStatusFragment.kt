package com.mobigod.statussaver.ui.create.fragment

import android.content.Context
import android.graphics.Typeface
import android.hardware.input.InputManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elyeproj.drawtext.dpToPx
import com.jakewharton.rxbinding2.widget.itemClicks
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.databinding.TypeStatusFragmentBinding
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.ui.create.adapters.ColorItem
import com.mobigod.statussaver.ui.create.adapters.ColorsAdapter
import com.mobigod.statussaver.ui.create.adapters.decorators.HorizontalSpacingDecorator
import com.mobigod.statussaver.ui.customviews.DrawTextView
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject
import kotlin.ClassCastException

class TypeStatusFragment: BaseFragment<TypeStatusFragmentBinding>(), AdapterView.OnItemSelectedListener {

    lateinit var binding: TypeStatusFragmentBinding

    override fun getLayoutRes() = R.layout.type_status_fragment
    private var typeStatusInterface: TypeStatusInterface? = null
    private val random = Random()
    private var fondRes: Int = 0
    private var  typedText: DrawTextView.TypedText? = null


    private val numToFontMap = mapOf(
        0 to R.font.acme,
        1 to R.font.aguafina_script,
        2 to R.font.allerta_stencil,
        3 to R.font.almendra_display,
        4 to R.font.annie_use_your_telescope,
        5 to R.font.audiowide,
        6 to R.font.bangers
    )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            typeStatusInterface = context as TypeStatusInterface
        }catch (e: ClassCastException){
            e.printStackTrace()
            throw  ClassCastException("Your class must implement interface: TypeStatusInterface")
        }
    }


    override fun onPause() {
        super.onPause()
        typeStatusInterface?.onTypeFragPaused()
        Log.i("typeStatus", "PAUSE CALLED!!!!!")
    }


    override fun onResume() {
        super.onResume()
        if (typedText != null){
            binding.statusEd.textSize = typedText!!.fontSize.toFloat()
            binding.statusEd.setTextColor(typedText!!.fontColor)
            binding.statusEd.typeface = getTypeFace(typedText!!.fontRes)
            //binding.statusEd.setText(typedText!!.typedText)
        }
       // Tools.showKeyboard(context!!)
    }



    private fun getTypeFace(fontRes: Int): Typeface? {
        if (fontRes == 0) {
            return Typeface.DEFAULT
        }
        return ResourcesCompat.getFont(context!!, fontRes)
    }


    override fun initComponents() {
        binding = getBinding()
        typedText = DrawTextView.TypedText(context!!)

        val colorsAdapter = ColorsAdapter()
        colorsAdapter.colorChooserInterface = object : ColorsAdapter.ColorChooser {
            override fun onColorChoosed(colorItem: ColorItem) {
                //use the color here
                typedText?.fontColor = ContextCompat.getColor(context!!, colorItem.color)
                binding.statusEd.setTextColor(ContextCompat.getColor(context!!, colorItem.color))
            }
        }

        binding.textColorsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingDecorator(5, resources))
            adapter = colorsAdapter
        }

        binding.finishBtn.setOnClickListener {
            typedText?.typedText = binding.statusEd.text.toString()
            typeStatusInterface?.onFinishedClicked(typedText!!)
        }

        binding.changeFontMv.setOnClickListener {
            val generatedNum = random.nextInt(numToFontMap.size)
            fondRes = numToFontMap[generatedNum] ?: R.font.acme
            typedText?.fontRes = fondRes
            binding.statusEd.typeface = ResourcesCompat.getFont(context!!, fondRes)
           // binding.statusEd.typeface = Typeface.createFromAsset(activity!!.assets, "fonts/acme.ttf")
            //show dialog to change font
        }

        setUpTextSizeSpinner()
    }

    private fun setUpTextSizeSpinner() {
        ArrayAdapter.createFromResource(context!!, R.array.text_size_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.textSizeDropdown.adapter = adapter
            binding.textSizeDropdown.onItemSelectedListener = this
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val value = parent?.getItemAtPosition(position) as String
        binding.statusEd.textSize = resources.dpToPx(value.toInt())
        typedText?.fontSize = value.toInt()
    }

    interface TypeStatusInterface {
        fun onFinishedClicked(typedText: DrawTextView.TypedText)
        fun onTypeFragPaused()
    }

}

class  FontListAdapter(val onFontItemClicked: (String) -> Unit): ListAdapter<String, FontListAdapter.fontItemVH>(object : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem

}){
    private var context: Context? = null
    private val fontList = mutableListOf<String>().apply {
        add("font/acme.ttf")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fontItemVH {
        context = parent.context
        val lInflater = LayoutInflater.from(parent.context)
        return fontItemVH(lInflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: fontItemVH, position: Int) {
        val tView = holder.itemView.findViewById<TextView>(android.R.id.text1)
        tView.typeface = Typeface.createFromAsset(context!!.assets, fontList[position])
        tView.text = getItem(position)
        onFontItemClicked(fontList[position])
    }


    inner class fontItemVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}