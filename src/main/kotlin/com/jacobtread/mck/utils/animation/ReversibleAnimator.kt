package com.jacobtread.mck.utils.animation

import com.jacobtread.mck.utils.time.Time

/**
 * ReversibleAnimator An animator that will reverse [start] and [end] when the
 * animation progress reaches 100%. Meaning when the animation is complete
 * it will replay the animation in reverse. Note [delay] will only be considered
 * at the start of each animation and will be ignored when reversing the animation
 *
 * @property function The animation function used to calculate progress
 * @constructor Create empty ReversibleAnimator
 */
class ReversibleAnimator(
    val function: AnimatorFunction,
) : DelayableAnimator {

    override var delay: Long = 0L

    override var start: Float = 0f
    override var end: Float = 0f
    override var duration: Long = 0L

    /**
     * reversed Whether the animation is playing in the reverse direction. This will
     * invert the progress calculation using (progress * (start - end)) + end instead
     * of (progress * (end - start)) + start
     */
    private var reversed = false

    /**
     * startTime The start time used when the animation is to prevent waiting for [delay]
     * when the animation becomes reversed
     */
    private var startTime = 0L

    /**
     * actualStartTime The actual time this animation will be able to start at
     * this is not affected by animation reversal and includes the [delay].
     *
     * The animation [get] function will return [start] or [end] respectively
     * if the current time has not passed this value
     */
    private var actualStartTime = 0L

    override fun start(start: Float, end: Float, duration: Long) {
        this.start = start
        this.end = end
        this.duration = duration
        val time = Time.getHighResolution()
        actualStartTime = time + delay
        startTime = actualStartTime
    }

    override fun get(): Float {
        val time = Time.getHighResolution()
        if (time < actualStartTime) return notStarted()
        val passed = time - startTime
        val progress: Float = passed / duration.toFloat()
        if (progress <= 0f) return notStarted()
        if (progress >= 1f) {
            return if (reversed) {
                actualStartTime = time + delay
                startTime = actualStartTime
                reversed = false
                start
            } else {
                startTime = time
                reversed = true
                end
            }
        }
        val funcProgress = function.calculate(progress)
        return if (reversed) {
            (funcProgress * (start - end)) + end
        } else {
            (funcProgress * (end - start)) + start
        }
    }

    /**
     * notStarted Used to return the default value for when the animation
     * has not yet started aka [actualStartTime] has not yet been reached
     *
     * @return Returns [start] if the animation is not reversed otherwise [end]
     */
    private fun notStarted(): Float = if (reversed) end else start
}