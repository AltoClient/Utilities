package com.jacobtread.mck.utils.animation

fun interface AnimatorFunction {

    /**
     * calculate Used to calculate what the provided
     * [progress] value would be in terms of this
     * functions' algorithm examples are [easeInOutSine]
     * and [easeOutQuart]
     *
     * @param progress A value between 1.0 and 0.0 represents the progress
     * from start to end
     * @return The new progress value
     */
    fun calculate(progress: Float): Float

}