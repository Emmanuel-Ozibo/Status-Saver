package com.mobigod.statussaver.ui.split.dialogs


import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.mobigod.statussaver.databinding.SplitDialogLayoutBinding
import java.util.*


class SplitVideoDialog: DialogFragment() {
    private lateinit var message: Message
    private val MESSAGE_KEY1 = "message_key1"

    private val bundle = Bundle()

    lateinit var binding: SplitDialogLayoutBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SplitDialogLayoutBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message = arguments?.get(MESSAGE_KEY1) as Message

        setListeners()

    }

    private fun setListeners() {

        bundle.putInt(PROGRESS, 30)

        binding.continueBtn.setOnClickListener {
            var folderName = binding.folderNameTv.text.toString()
                .trim()

            if (folderName.isEmpty())
                folderName = UUID.randomUUID().toString()


            bundle.putString(FOLDER_NAME, folderName)
            message.data = bundle
            message.sendToTarget()

            dismiss()
        }

        binding.splitDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.percentageTv.text = "${progress}secs"
                bundle.putInt(PROGRESS, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    companion object {

        const val PROGRESS = "progress"
        const val FOLDER_NAME = "f_name"

        fun getInstance(message: Message) =
            SplitVideoDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(MESSAGE_KEY1, message)
                }
            }

    }
}