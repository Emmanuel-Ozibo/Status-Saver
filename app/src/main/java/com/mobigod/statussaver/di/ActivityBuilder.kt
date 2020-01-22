package com.mobigod.statussaver.di

import com.mobigod.statussaver.ui.create.activity.StatusCreatorActivity
import com.mobigod.statussaver.ui.create.fragment.EmojisFragment
import com.mobigod.statussaver.ui.create.fragment.SongsFragment
import com.mobigod.statussaver.ui.create.fragment.TypeStatusFragment
import com.mobigod.statussaver.ui.saver.activity.StatusSaverActivity
import com.mobigod.statussaver.ui.saver.activity.VideoPlayerActivity
import com.mobigod.statussaver.ui.saver.fragment.StatusImagesFragment
import com.mobigod.statussaver.ui.saver.fragment.StatusVideosFragment
import com.mobigod.statussaver.ui.splash.SplashActivity
import com.mobigod.statussaver.ui.split.SplitVideoActivity
import com.mobigod.statussaver.ui.split.fragments.SplitFoldersFragment
import com.mobigod.statussaver.ui.split.fragments.SplitVideoFoldersFragment
import com.mobigod.statussaver.ui.split.fragments.SplitVideoFragment
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
    abstract fun contributeEmojisFragmentInjector(): EmojisFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoActivityInjector(): VideoPlayerActivity

    @ContributesAndroidInjector
    abstract fun contributeStatusCreatorInjector(): StatusCreatorActivity

    @ContributesAndroidInjector
    abstract fun contributeSplitActivity(): SplitVideoActivity

    @ContributesAndroidInjector
    abstract fun contributeSplitVideoFolderFragmentInjector(): SplitFoldersFragment

    @ContributesAndroidInjector
        abstract fun contributeSplitVideoFragmentInjector(): SplitVideoFragment
}