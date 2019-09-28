package com.mobigod.statussaver.ui.saver.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

class StatusSaverActivity: BaseActivity<ActivityStatusSaverBinding>() {

    lateinit var binding: ActivityStatusSaverBinding

    private val TAG = "StatusSaverActivity"
    private val READ_EXTERNAL_STORAGE_ID = 1


    override fun initComponent() {
        //this is just like onCreate method
        binding = getBinding()
        binding.ssaverToolbar.toolbar.title = "Status Saver"

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

            else -> {
                // Ignore all other requests.
            }
        }
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