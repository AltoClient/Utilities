package com.jacobtread.mck.utils

interface ProgressListener {
    fun setTitle(value: String);
    fun setText(value: String);
    fun setProgress(value: Int);
    fun setDoneWorking() {}
}