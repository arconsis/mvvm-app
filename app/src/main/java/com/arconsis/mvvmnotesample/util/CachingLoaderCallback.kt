package com.arconsis.mvvmnotesample.util

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

/**
 * Created by Alexander on 11.05.2017.
 */
abstract class CachingLoaderCallback<T>(private val context: Context, private val init: () -> T) : LoaderManager.LoaderCallbacks<T> {
    override fun onLoaderReset(loader: Loader<T>?) {
        // nothing to do
    }

    override fun onLoadFinished(loader: Loader<T>?, data: T?) {
        if (data != null) {
            onLoad(data)
        } else {
            onUnload()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
        return CachingLoader(context, init)
    }

    abstract fun onLoad(data: T)

    abstract fun onUnload()
}