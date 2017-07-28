package com.arconsis.mvvmnotesample.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.annotation.Nullable
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        super.observe(owner, PendingObserver(observer))
    }

    @MainThread
    override fun setValue(@Nullable t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    private inner class PendingObserver<T>(private val observer: Observer<T>) : Observer<T> {
        override fun onChanged(t: T?) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}