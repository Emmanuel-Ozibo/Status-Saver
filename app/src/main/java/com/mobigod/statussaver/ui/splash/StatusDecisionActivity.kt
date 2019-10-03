package com.mobigod.statussaver.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobigod.statussaver.R
import com.mobigod.statussaver.ui.create.activity.StatusCreatorActivity
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
import kotlinx.android.synthetic.main.activity_status_decision.*

class StatusDecisionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_decision)

        download_status.setOnClickListener {
            StatusSaverActivity.start(this)
        }

        create_status.setOnClickListener {
            StatusCreatorActivity.start(this)
        }
    }
}
