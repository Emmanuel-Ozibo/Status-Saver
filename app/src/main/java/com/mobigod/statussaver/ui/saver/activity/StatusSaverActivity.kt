package com.mobigod.statussaver.ui.saver.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.gms.ads.MobileAds
import com.himangi.imagepreview.PreviewFile
import com.mobigod.statussaver.BuildConfig
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseActivity
import com.mobigod.statussaver.databinding.ActivityStatusSaverBinding
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith
import com.mobigod.statussaver.ui.saver.adapter.StatusPagerAdapter
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import android.media.MediaRecorder.AudioSource.MIC
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import android.R.attr.colorPrimaryDark
import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.IConvertCallback
import cafe.adriel.androidaudioconverter.model.AudioFormat
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.mobigod.lib_audio_cutter.AudioCutter
import com.mobigod.statussaver.data.local.PreferenceManager
import java.io.File
import java.lang.Exception
import javax.inject.Inject


class StatusSaverActivity: BaseActivity<ActivityStatusSaverBinding>() {

    lateinit var binding: ActivityStatusSaverBinding
    @Inject lateinit var prefManager: PreferenceManager

    private val TAG = "StatusSaverActivity"
    private val READ_EXTERNAL_STORAGE_ID = 1
    private val AUDIO_REQUEST_CODE = 0
    private val RECORD_AUDIO_ID = 2
    private val AUDIO_CUTTER_CODE = 3


    override fun initComponent() {
        //this is just like onCreate method
        binding = getBinding()
        binding.ssaverToolbar.toolbar.title = "Status Saver"

        if (binding.ssaverToolbar.toolbar != null){
            setSupportActionBar(binding.ssaverToolbar.toolbar)
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this,
            getString(R.string.admob_app_id))


        val actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout,
            binding.ssaverToolbar.toolbar,
            R.string.open_drawer, R.string.close_drawer)

        binding.navView.getHeaderView(0)
            .rootView.findViewById<TextView>(R.id.version_number)
            .text = BuildConfig.VERSION_NAME


        actionBarDrawerToggle.syncState()

        if(!Tools.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Tools.askReadStoragePermission(this, READ_EXTERNAL_STORAGE_ID)
        }else{
            //set up pager adapter
            setUpView()
        }

    }

    override fun hasAndroidInjector(): Boolean = true

    private fun setUpView() {
        //set up pager adapter
        binding.viewPager.adapter = StatusPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.rec_audio -> {
                //ask for runtime permission
                if(!Tools.checkPermission(this, Manifest.permission.RECORD_AUDIO)){
                    Tools.askRecordAudioPermission(this, RECORD_AUDIO_ID)
                    return false
                }

                lunchAudioRecorder()
            }
        }
        return true
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


    override fun getLayoutRes(): Int {
        return R.layout.activity_status_saver
    }


    private fun convertAndShareFile() {
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
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //start audio conversion
                longToastWith("File saved at: ${prefManager.currentFileRecord}")
                convertAndShareFile()

            } else if (resultCode == RESULT_CANCELED) {
                longToastWith("Audio recording cancelled.")
            }
        }else if (requestCode == AUDIO_CUTTER_CODE) {
            if (resultCode == Activity.RESULT_OK){
                val dataUri = data?.data
                if (dataUri != null) {
                    longToastWith("${dataUri.path}")
                }
            }else {
                longToastWith("Audio status cancelled")
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //set up pager adapter
                    setUpView()

                } else {
                    longToastWith("You must accept read external storage permission to continue")
                    finish()
                }
                return
            }

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
        fun start(context: Context) {
            Intent(context, StatusSaverActivity::class.java).apply {
                //put that ever you like here
            }.also {
                context.startActivity(it)
            }
        }
    }

}