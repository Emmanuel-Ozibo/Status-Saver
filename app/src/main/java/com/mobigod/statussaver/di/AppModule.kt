package com.mobigod.statussaver.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mobigod.statussaver.data.local.PreferenceManager
import com.mobigod.statussaver.data.repo.StatusSaverRepo
import com.mobigod.statussaver.data.local.FileSystemManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppName() = "Status Saver"

    @Provides
    @Singleton
    fun provideAppContext(app: Application): Context = app

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context) =
        context.getSharedPreferences("status_saver_pref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providePreferenceManager(pref: SharedPreferences): PreferenceManager {
        return PreferenceManager(pref)
    }

    @Provides
    @Singleton
    fun provideFileManager(context: Context) = FileSystemManager(context)


    @Provides
    @Singleton
    fun provideStatusSaverRepo(fileSystemManager: FileSystemManager) =
        StatusSaverRepo(fileSystemManager)
}