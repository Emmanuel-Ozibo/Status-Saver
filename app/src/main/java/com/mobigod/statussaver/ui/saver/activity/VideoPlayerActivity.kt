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
import com.snatik.storage.Storage
import android.os.Environment
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith


class VideoPlayerActivity: AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player)

        val mVideoUrl = intent.getStringExtra(Constants.INTENT_VIDEO_URL)

        binding.player.apply {
            setSource(File(mVideoUrl).getUri())
            removeCaptions()
        }

        binding.saveVid.setOnClickListener {
            /*val nd = Environment.getExternalStorageDirectory()
            val toPath = nd.absolutePath + "${getString(R.string.app_name)}/${File(mVideoUrl).name}.mp4"
            val storage = Storage(applicationContext)
            storage.copy(mVideoUrl, toPath)*/

            longToastWith("Not yet implemented!!!!!,\n You can send in a PR to the project on github:)")

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
        fun start(context: Context, videoUrl: String){
            Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(Constants.INTENT_VIDEO_URL, videoUrl)
            }.also {
                context.startActivity(it)
            }
        }
    }

}