package com.mobigod.statussaver.ui.saver.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseActivity
import com.mobigod.statussaver.databinding.ActivityVideoPlayerBinding
import com.mobigod.statussaver.global.Constants
import com.mobigod.statussaver.global.getUri
import java.io.File
import android.os.Environment
import android.widget.Toast
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith
import javax.inject.Inject


class VideoPlayerActivity: BaseActivity<ActivityVideoPlayerBinding>() {

    @Inject lateinit var fileSystemManager: FileSystemManager
    lateinit var binding: ActivityVideoPlayerBinding


    override fun initComponent() {
        binding = getBinding()

        val mVideoUrl = intent.getStringExtra(Constants.INTENT_VIDEO_URL)
        val file = File(mVideoUrl)


        binding.player.apply {
            setSource(file.getUri())
            removeCaptions()
        }

        binding.saveVid.setOnClickListener {
            //fileSystemManager.saveStatus(file.absolutePath)
            fileSystemManager.saveVideoFile(this, file.absolutePath)
            longToastWith("Well done, Status saved")

        }

        binding.shareVid.setOnClickListener {
            Tools.share(this, mVideoUrl)
        }

    }

    override fun hasAndroidInjector(): Boolean = true

    override fun getLayoutRes(): Int =  R.layout.activity_video_player

    override fun onPause() {
        super.onPause()
        binding.player.pause()
    }


    override fun onResume() {
        super.onResume()
        if(!binding.player.isPlaying()){
            binding.player.start()
        }

    }


    companion object {
        fun start(context: Context, videoUrl: String) {
            Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(Constants.INTENT_VIDEO_URL, videoUrl)
            }.also {
                context.startActivity(it)
            }
        }
    }

}