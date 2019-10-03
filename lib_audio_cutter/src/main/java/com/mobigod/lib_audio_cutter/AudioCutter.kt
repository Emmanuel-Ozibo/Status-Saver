package com.mobigod.lib_audio_cutter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt

class AudioCutter constructor(val activity: Activity) {
    private var mFileName: String = ""
    private var mCode = 0
    @ColorInt
    private var color = 1

    fun setFileName(fileName: String) = apply { this.mFileName = fileName }
    fun setRequestCode(code: Int) = apply { this.mCode = code }
    fun setColor(colorRes: Int) = apply { this.color = colorRes }


    fun startCutter() {
        try {
            Intent(activity, RingdroidEditActivity::class.java).apply {
                data = Uri.parse(mFileName)
                putExtra("was_get_content_intent", true)
                /*setClassName("com.mobigod.lib_audio_cutter",
                    "com.mobigod.lib_audio_cutter.RingdroidEditActivity")*/
            }.also {
                //activity.startActivity(it)
                activity.startActivityForResult(it, mCode)
            }

        } catch (e: Exception) {
            Log.e("Ringdroid", "Couldn't start editor")
        }

    }

    companion object {
        fun with(context: Activity) = AudioCutter(context)
    }
}