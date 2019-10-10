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

        setUpView()
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
            /*R.id.rec_audio -> {
                //ask for runtime permission
                if(!Tools.checkPermission (this, Manifest.permission.RECORD_AUDIO)){
                    Tools.askRecordAudioPermission(this, RECORD_AUDIO_ID)
                    return false
                }

                lunchAudioRecorder()
            }*/
        }
        return true
    }


    override fun getLayoutRes(): Int {
        return R.layout.activity_status_saver
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