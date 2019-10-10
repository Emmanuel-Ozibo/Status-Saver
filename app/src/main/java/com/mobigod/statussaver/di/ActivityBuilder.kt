package com.mobigod.statussaver.di

import com.mobigod.statussaver.ui.create.activity.StatusCreatorActivity
import com.mobigod.statussaver.ui.create.fragment.SongsFragment
import com.mobigod.statussaver.ui.create.fragment.TypeStatusFragment
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
import com.mobigod.statussaver.ui.saver.activity.VideoPlayerActivity
import com.mobigod.statussaver.ui.saver.fragment.StatusImagesFragment
import com.mobigod.statussaver.ui.saver.fragment.StatusVideosFragment
import com.mobigod.statussaver.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun contributeSplashActivityInjector(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeStatusSaverActivityInjector(): StatusSaverActivity

    @ContributesAndroidInjector
    abstract fun contributeStatusSaverFragmentInjector(): StatusImagesFragment

    @ContributesAndroidInjector
    abstract fun contributeStatusSaverVideoFragmentInjector(): StatusVideosFragment

    @ContributesAndroidInjector
    abstract fun contributeTypeStatusFragmentInjector(): TypeStatusFragment

    @ContributesAndroidInjector
    abstract fun contributeSongsFragmentInjector(): SongsFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoActivityInjector(): VideoPlayerActivity

    @ContributesAndroidInjector
    abstract fun contributeStatusCreatorInjector(): StatusCreatorActivity

}