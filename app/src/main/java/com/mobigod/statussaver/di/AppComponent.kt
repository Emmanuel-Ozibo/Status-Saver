package com.mobigod.statussaver.di

import android.app.Application
import com.mobigod.statussaver.StatusSaverApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.BindsInstance
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,
        ActivityBuilder::class, AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: StatusSaverApp)

}