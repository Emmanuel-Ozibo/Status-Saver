package com.mobigod.statussaver.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobigod.statussaver.R
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith
import com.mobigod.statussaver.ui.create.activity.StatusCreatorActivity
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
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
