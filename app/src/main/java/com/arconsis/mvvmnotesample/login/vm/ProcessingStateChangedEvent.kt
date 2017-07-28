package com.arconsis.mvvmnotesample.login.vm

data class ProcessingStateChangedEvent<T>(val state: ProcessingState, val data: T? = null)