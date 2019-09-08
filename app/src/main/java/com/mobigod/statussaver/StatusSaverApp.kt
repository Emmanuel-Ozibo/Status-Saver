package com.mobigod.statussaver

import android.app.Activity
import android.app.Application
import com.mobigod.statussaver.di.AppComponent
import com.mobigod.statussaver.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class StatusSaverApp: Application(), HasAndroidInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

         DaggerAppComponent
            .builder()
            .application(this)
            .build()
             .inject(this)

    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityDispatchingAndroidInjector
    }

}