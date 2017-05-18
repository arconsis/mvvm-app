package com.arconsis.mvvmnotesample.util

import android.content.Context
import android.support.v4.content.Loader

/**
 * Created by Alexander on 11.05.2017.
 */
class CachingLoader<T>(context: Context, private val init: () -> T) : Loader<T>(context) {
    private var cached: T? = null

    override fun onStartLoading() {
        val vm = if (cached != null) {
            cached
        } else {
            cached = init()
            cached
        }
        deliverResult(vm)
    }

    override fun onStopLoading() {
        deliverResult(null)
    }

    override fun onReset() {
        cached = null
    }
}