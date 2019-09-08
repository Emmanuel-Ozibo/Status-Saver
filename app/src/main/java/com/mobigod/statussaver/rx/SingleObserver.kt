package com.mobigod.statussaver.rx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class SingleObserver<T>: Observer<T> {

    override fun onComplete() {
        //do nothing
    }

    override fun onSubscribe(d: Disposable) {

    }

}