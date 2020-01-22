package com.mobigod.statussaver.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mobigod.statussaver.R
import com.mobigod.statussaver.global.longToastWith
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<T: ViewDataBinding>: DaggerFragment() {

    lateinit var viewDataBinding: T

    val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        return viewDataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    protected fun showToast(message: String) {
        activity!!.longToastWith(message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun initComponents()

    fun getBinding(): T {
        return  viewDataBinding
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}