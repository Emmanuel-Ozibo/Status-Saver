package com.mobigod.statussaver.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobigod.statussaver.R
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith
import com.mobigod.statussaver.ui.create.activity.StatusCreatorActivity
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
import com.mobigod.statussaver.ui.split.SplitVideoActivity
import kotlinx.android.synthetic.main.activity_status_decision.*

class StatusDecisionActivity : AppCompatActivity() {

    private val READ_EXTERNAL_STORAGE_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_decision)

        if(!Tools.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Tools.askReadStoragePermission(this, READ_EXTERNAL_STORAGE_ID)
        }

        download_status.setOnClickListener {
            StatusSaverActivity.start(this)
        }

        create_status.setOnClickListener {
            StatusCreatorActivity.start(this)
        }

        split_video.setOnClickListener {
            SplitVideoActivity.start(this)
        }

        share_app_link.setOnClickListener {
                val textMessage = "Hello dear, I have been using ${getString(R.string.app_name)} for FREE to prepare " +
                        "for my exams and it has been awesome \n" +
                        "download it now from google playstore via this link ${"http://play.google.com/store/apps/details?id=com.emeecodes.schoolprep"}"
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, textMessage)
                    type = "text/plain"
                    startActivity(Intent.createChooser(this, getString(R.string.invite_string)))
                }
        }

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            READ_EXTERNAL_STORAGE_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //do nothing for now
                } else {
                    longToastWith("You must accept read external storage permission to continue")
                    finish()
                }
                return
            }
        }
    }


}
