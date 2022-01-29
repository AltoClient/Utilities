package com.jacobtread.alto.utils.animation

import com.jacobtread.alto.utils.time.Time

/**
 * SimpleAnimator A simple implementation of animator that once the animation
 * is complete will continue to return the [end] value unless a new animation
 * is started with [start]. Animations will not start until the provided [delay]
 * has been reached since [start] was called
 *
 * @property function The animation function used to calculate progress
 * @constructor Create empty SimpleAnimator
 */
class SimpleAnimator(
    val function: AnimatorFunction,
) : DelayableAnimator {

    override var delay: Long = 0L

    override var start: Float = 0f
    override var end: Float = 0f
    override var duration: Long = 0L

    /**
     * startTime The time when the animation started / should start the [delay]
     * value is appended to this
     */
    private var startTime = 0L

    override fun start(start: Float, end: Float, duration: Long) {
        this.start = start
        this.end = end
        this.duration = duration
        startTime = Time.getHighResolution() + delay
    }

    override fun get(): Float {
        val time = Time.getHighResolution()
        if (startTime > time) return start
        val passed = time - startTime
        val progress = passed.toFloat() / duration.toFloat()
        if (progress <= 0f) return start
        if (progress >= 1f) return end
        val funcProgress = function.calculate(progress)
        return (funcProgress * (end - start)) + start
    }
}