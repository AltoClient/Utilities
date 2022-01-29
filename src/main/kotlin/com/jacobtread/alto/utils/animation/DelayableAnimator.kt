package com.jacobtread.alto.utils.animation

interface DelayableAnimator : Animator {

    /**
     * delay A delay which should be waited before the animation
     * runs. In the case of [ReversibleAnimator] this is also the
     * duration waited before repeating the next iteration
     */
    var delay: Long

    /**
     * startWithDelay Starts a new animation with the provided [delay] the delay
     * is set to the delay value and will be used by the animator. Wraps the
     * [start] function to allow setting a delay before the animation starts
     *
     * @see start
     * @param start
     * @param end
     * @param duration
     * @param delay
     */
    fun startWithDelay(start: Float, end: Float, duration: Long, delay: Long) {
        this.delay = delay
        start(start, end, duration)
    }
}
