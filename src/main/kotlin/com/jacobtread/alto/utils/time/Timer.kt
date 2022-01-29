package com.jacobtread.alto.utils.time

/**
 * Timer A simple timer task for checking if a certain amount of time
 * in milliseconds has passed (after it has passed a call to [reset]
 * must be made to update the [lastTime] otherwise subsequent calls will
 * all return true) NOT THREAD SAFE please synchronize if using across threads
 *
 * @constructor Create empty Timer
 */
class Timer {

    /**
     * lastTime The last stored time updated when [reset] is
     * called. Used to calculate the duration passed and compare
     * inside of [hasElapsed]
     */
    private var lastTime = Time.getHighResolution()

    /**
     * hasElapsed Checks to see if the provided [duration] has
     * passed since the last time that [reset] was called or since
     * the program started (if reset hasn't been called yet) make
     * sure to call [reset] after a successful result from this function
     *
     * @param duration The duration to check if passed
     * @return Whether the duration has passed
     */
    fun hasElapsed(duration: Long): Boolean {
        val time = Time.getHighResolution()
        return time - lastTime >= duration
    }

    /**
     * reset Resets the timer to the current high resolution
     * time in milliseconds
     */
    fun reset() {
        lastTime = Time.getHighResolution()
    }

}