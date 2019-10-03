package com.mobigod.statussaver.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mobigod.statussaver.R
import com.mobigod.statussaver.data.local.PreferenceManager
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (!preferenceManager.isFirstTime){
                Intent(this, StatusDecisionActivity::class.java).also {
                    startActivity(it)
                    finish()
                }

            }
        }, 2000)
    }

}
