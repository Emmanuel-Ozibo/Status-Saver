package com.mobigod.statussaver.ui.create.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobigod.statussaver.R
import com.mobigod.statussaver.databinding.BrushSettingLayoutBinding
import com.mobigod.statussaver.ui.create.adapters.ColorItem
import com.mobigod.statussaver.ui.create.adapters.ColorsAdapter
import java.lang.ClassCastException

class BrushSettingsSheet: BottomSheetDialogFragment() {

    lateinit var listener: BrushSettingsSheetListener
    lateinit var binding: BrushSettingLayoutBinding

    private var brushSize = 0f
    private var mColorItem: ColorItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as BrushSettingsSheetListener
        }catch (e: ClassCastException){
            throw ClassCastException("Implement the interface of this class")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BrushSettingLayoutBinding.bind(inflater.inflate(R.layout.brush_setting_layout, container, false))
        binding.doneBtn.setOnClickListener {
            listener.colorSelected(mColorItem, brushSize)
            dismiss()

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        brushSize = arguments!!.getFloat("arg1")
        setTheBrushSizeLevel()


        binding.brushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    //progress is in percentage
                    //the brush size should be btw 10 and 70, so scale this to the 0 to 100
                    brushSize = (progress / 100f) * 60
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        val colorsAdapter = ColorsAdapter()
        colorsAdapter.colorChooserInterface = object : ColorsAdapter.ColorChooser {
            override fun onColorChoosed(colorItem: ColorItem) {
                //use the color here
                mColorItem = colorItem
            }
        }

        binding.brushColorsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }

    }

    private fun setTheBrushSizeLevel() {
        val currentBrushSize = arguments!!.getFloat("arg1")
        val percent = (currentBrushSize / 60) * 100
        binding.brushSize.progress = percent.toInt()
    }


    companion object {
        fun newInstance(currentBrushSize: Float) =
            BrushSettingsSheet().apply {
                arguments = Bundle().apply {
                    putFloat("arg1", currentBrushSize)
                }
            }
    }


    interface BrushSettingsSheetListener {
        fun colorSelected(colorItem: ColorItem?, brushSize: Float)
    }
}