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
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith


class VideoPlayerActivity: AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player)

        val mVideoUrl = intent.getStringExtra(Constants.INTENT_VIDEO_URL)
        val file = File(mVideoUrl)


        binding.player.apply {
            setSource(file.getUri())
            removeCaptions()
        }

        binding.saveVid.setOnClickListener {
            Tools.saveStatus(file.absolutePath)
            longToastWith("Well done, Status saved")

        }

        binding.shareVid.setOnClickListener {
            Tools.share(this, mVideoUrl)
        }


    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
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