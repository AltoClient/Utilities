package com.jacobtread.alto.utils.time

object Time {

    /**
     * getHighResolution Gets a high resolution version of the system
     * time in milliseconds by converting the [System.nanoTime] to
     * milliseconds
     *
     * @return The system time in milliseconds
     */
    @JvmStatic
    fun getHighResolution(): Long {
        return System.nanoTime() / 1000000L
    }

}