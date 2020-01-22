package com.mobigod.statussaver.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mobigod.statussaver.R
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<T: ViewDataBinding>: DaggerAppCompatActivity(){

    lateinit var viewDataBinding: T

    val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (hasAndroidInjector()){
            AndroidInjection.inject(this)
        }

        super.onCreate(savedInstanceState)
        performDataBinding()
        //setContentView(getLayoutRes())
        initComponent()
    }

    open fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutRes())
    }

    fun getBinding(): T = viewDataBinding

    abstract fun initComponent()
    abstract fun hasAndroidInjector(): Boolean


    override fun startActivity(intent: Intent){
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }


    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}