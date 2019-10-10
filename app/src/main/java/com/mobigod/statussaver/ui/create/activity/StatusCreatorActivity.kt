package com.mobigod.statussaver.ui.create.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.StatusBuilderBaseActivity
import com.mobigod.statussaver.databinding.StatusCreatorLayoutBinding
import com.mobigod.statussaver.ui.create.adapters.DecorationToolsAdapter
import com.mobigod.statussaver.ui.create.fragment.TypeStatusFragment
import com.mobigod.statussaver.ui.customviews.DrawTextView
import android.graphics.Color
import com.mobigod.statussaver.global.*
import com.mobigod.statussaver.ui.create.adapters.decorators.HorizontalSpacingDecorator
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.security.SecureRandom
import android.app.Activity
import android.graphics.Bitmap
import android.R.attr.path
import android.content.pm.PackageManager
import android.graphics.Rect
import com.bumptech.glide.Glide
import android.graphics.drawable.Drawable
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.IConvertCallback
import cafe.adriel.androidaudioconverter.model.AudioFormat
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mobigod.lib_audio_cutter.AudioCutter
import com.mobigod.statussaver.data.local.PreferenceManager
import com.mobigod.statussaver.data.model.MusicFile
import com.mobigod.statussaver.databinding.AudioItemLayoutBinding
import com.mobigod.statussaver.ui.create.adapters.ColorItem
import com.mobigod.statussaver.ui.create.adapters.ColorsAdapter
import com.mobigod.statussaver.ui.create.fragment.BrushSettingsSheet
import com.mobigod.statussaver.ui.create.fragment.SongsFragment
import com.mobigod.statussaver.ui.customviews.PanViewsLayout
import kotlinx.android.synthetic.main.audio_item_layout.view.*
import java.io.File
import java.io.FileReader
import java.lang.Exception
import javax.inject.Inject


class StatusCreatorActivity: StatusBuilderBaseActivity<StatusCreatorLayoutBinding>(),
    TypeStatusFragment.TypeStatusInterface,
    BrushSettingsSheet.BrushSettingsSheetListener,
    PopupMenu.OnMenuItemClickListener,
    PanViewsLayout.FingerListener,
    SongsFragment.SongsFragmentListner {

    lateinit var binding: StatusCreatorLayoutBinding
    @Inject
    lateinit var prefManager: PreferenceManager

    override fun hasAndroidInjector() = true
    override fun getLayoutRes() = R.layout.status_creator_layout

    private var textStatusFragment: Fragment? = null

    lateinit var decorationToolsAdapter: DecorationToolsAdapter

    override fun initComponent() {
        binding = getBinding()

        textStatusFragment = TypeStatusFragment()

        setUpDecorationTools()
        binding.panViewLayout.listener = this


        binding.decoToolsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingDecorator(15, resources))
            adapter = decorationToolsAdapter
        }

        binding.drawTxtView.setOnClickListener {
            binding.reLayout.hideWithTime(400)
            startFragment(R.id.edit_fragment_container, textStatusFragment!!, TypeStatusFragment::class.simpleName)
        }

    }

    override fun onFingerMove() {
        binding.decoToolsRv.hide()
        //todo: show the waste basket to delete
    }

    override fun onFingerStopMove() {
        binding.decoToolsRv.show()
        //todo: hide the waste basket to delete
    }

    override fun onSongsFragmentClosed() {
        binding.reLayout.show()
    }

    override fun onSongPicked(musicFile: MusicFile) {
        popFragment()
        prefManager.currentFileRecord = musicFile.path
        startAudioTrim(File(musicFile.path))
    }

    private fun setUpDecorationTools() {
        decorationToolsAdapter = DecorationToolsAdapter {
                decorationToolsItem, view ->
            when(decorationToolsItem.title){
                "Background" ->  {
                    if (binding.drawTxtView.isImageBackground){
                        //Rect
                        longToastWith("Will you like to remove this image?")
                        return@DecorationToolsAdapter
                    }
                    binding.drawTxtView.canvasColor = Tools.generateRandomColor()
                }

                "Picture" -> {
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this)
                }

                "Brush" -> {
                    if (binding.drawTxtView.paintBrushMode) {
                        //show bottomsheet to do some settings
                        showBottomSheetFragment(BrushSettingsSheet.newInstance(binding.drawTxtView.paintBrushSize),
                            BrushSettingsSheet::class.simpleName)
                        return@DecorationToolsAdapter
                    }
                    binding.drawTxtView.paintBrushMode = true
                }

                "Emoji" -> {
                    binding.drawTxtView.paintBrushColor = Color.TRANSPARENT
                }

                "Audio" -> {
                    //ask for runtime permission
                    if(!Tools.checkPermission(this, Manifest.permission.RECORD_AUDIO)){
                        Tools.askRecordAudioPermission(this, RECORD_AUDIO_ID)
                        return@DecorationToolsAdapter
                    }
                    showMenu(view)
                }
                else -> {
                    longToastWith(decorationToolsItem.title)
                }
            }
        }
    }

    fun showMenu(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(this@StatusCreatorActivity)
            inflate(R.menu.audio_picker_menu)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.record_audio -> {
                MediaStore.Audio.Media.DATA
                lunchAudioRecorder()
                true
            }
            R.id.audio_file -> {
                lunchAudioFilePicker()
                true
            }
            else -> false
        }
    }

    private fun lunchAudioFilePicker() {
        binding.reLayout.hideWithTime(400)
        startFragment(R.id.edit_fragment_container, SongsFragment(), SongsFragment::class.simpleName)

    }


    private fun lunchAudioRecorder() {
        //val folder = "${Environment.getExternalStorageDirectory()}/Status Saver Records"
        val recordingFolder = File(Environment.getExternalStorageDirectory(), "Status Saver Records")

        if (!recordingFolder.exists()) {
            recordingFolder.mkdir()
        }

        val filePath = "${recordingFolder.absolutePath}/recorded_audio${getFileId()}.wav"
        prefManager.currentFileRecord = filePath

        val color = resources.getColor(R.color.colorPrimaryDark)

        AndroidAudioRecorder.with(this)
            // Required
            .setFilePath(filePath)
            .setColor(color)
            .setRequestCode(AUDIO_REQUEST_CODE)

            // Optional
            .setSource(AudioSource.MIC)
            .setChannel(AudioChannel.STEREO)
            .setSampleRate(AudioSampleRate.HZ_48000)
            .setAutoStart(false)
            .setKeepDisplayOn(true)

            // Start recording
            .record()
    }

    private fun getFileId(): String {
        return SystemClock.currentThreadTimeMillis().toString()
    }

    override fun colorSelected(colorItem: ColorItem?, brushSize: Float) {
        if (colorItem != null){
            binding.drawTxtView.paintBrushColor = ContextCompat.getColor(this, colorItem.color)
        }
        binding.drawTxtView.paintBrushSize = brushSize
    }

    override fun onTypeFragPaused() {
        if (!binding.reLayout.isShowing()){
            binding.reLayout.show()
        }
    }


    override fun onFinishedClicked(typedText: DrawTextView.TypedText) {
        binding.drawTxtView.textTypedObj = typedText
        popFragment()
    }


    private fun convertSoundFile() {
        val convertCallback = object : IConvertCallback {
            override fun onSuccess(convertedFile: File?) {
                longToastWith("File conversion finished!!!: ${convertedFile?.absolutePath}")
                //Tools.share(this@StatusSaverActivity, convertedFile!!.absolutePath)
                startAudioTrim(convertedFile!!)
            }

            override fun onFailure(error: Exception?) {
                longToastWith("An Error occurred while converting this file")
            }
        }
        AndroidAudioConverter.with(this)
            // Your current audio file
            .setFile(File(prefManager.currentFileRecord))
            // Your desired audio format
            .setFormat(AudioFormat.MP3)
            // An callback to know when conversion is finished
            .setCallback(convertCallback)
            // Start conversion
            .convert()
    }



    private fun startAudioTrim(file: File) {
        val colorInt = R.color.colorPrimaryDark
        AudioCutter.with(this)
            .setFileName(file.absolutePath)
            .setRequestCode(AUDIO_CUTTER_CODE)
            .setColor(colorInt)
            .startCutter()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                Glide.with(this)
                    .asBitmap()
                    .load(resultUri)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            binding.drawTxtView.backgroundImageBmp = resource
                        }

                    })
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error

            }
        }else if (requestCode == AUDIO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //start audio conversion
                longToastWith("File saved at: ${prefManager.currentFileRecord}")
                startAudioTrim(File(prefManager.currentFileRecord))

            } else if (resultCode == RESULT_CANCELED) {
                longToastWith("Audio recording cancelled.")
            }
        }else if (requestCode == AUDIO_CUTTER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val filePath = data?.getStringExtra("new_path")
                prefManager.currentFileRecord = filePath!!
                addAudioMiniView()
            }else {
                longToastWith("Audio status cancelled")
            }
        } /*else if (requestCode == PICK_AUDIO_FILE_CODE) {
            if (resultCode == Activity.RESULT_OK){
                val dataUri = data?.data
                if (dataUri != null) {
                    val path = File(dataUri.toString()).absolutePath
                    longToastWith("Picked File at: $path")
                    prefManager.currentFileRecord = path
                    convertAndShareFile()
                }
            }else {
                longToastWith("Audio file picker failed")
            }
        }*/

    }

    private fun addAudioMiniView() {
        val lInflater = LayoutInflater.from(this)
        val audioMiniView = AudioItemLayoutBinding.inflate(lInflater).root
        audioMiniView.song_name_tv.text = File(prefManager.currentFileRecord).name
        binding.panViewLayout.addView(audioMiniView)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            RECORD_AUDIO_ID -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //set up pager adapter
                    lunchAudioRecorder()

                } else {
                    longToastWith("To continue, You have to allow")
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    companion object {

        private val PICK_AUDIO_FILE_CODE = 1
        private val RECORD_AUDIO_ID = 2
        private val AUDIO_CUTTER_CODE = 3
        private val AUDIO_REQUEST_CODE = 0


        fun start(context: Context) {
            Intent(context, StatusCreatorActivity::class.java).apply {
                //put that ever you like here
            }.also {
                context.startActivity(it)
            }
        }
    }
}