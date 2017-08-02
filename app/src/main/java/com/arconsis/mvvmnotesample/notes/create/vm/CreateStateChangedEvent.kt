package com.arconsis.mvvmnotesample.notes.create.vm

/**
 * Created by Alexander on 02.08.2017.
 */
data class CreateStateChangedEvent<T>(val state: CreateState, val data: T? = null)