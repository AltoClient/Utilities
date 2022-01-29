package com.jacobtread.mck.utils.animation

import kotlin.reflect.KProperty

/**
 * Animator Structure layout class for an animator which can
 * animate between the provided [start] and [end]
 * over the provided duration and provided a number in between
 * based on whatever animation function is being used
 *
 * @constructor Create empty Animator
 */
interface Animator {

    /**
     * start The backing value stored from [start] represents
     * the value that this animation should provide when
     * start
     */
    var start: Float

    /**
     * end The backing value stored from [start] represents
     * the value that this animation should provide when
     * ended
     */
    var end: Float

    /**
     * duration The backing value stored from [start] represents
     * how long this animation should last
     */
    var duration: Long

    /**
     * start Starts a new animation that will last [duration] milliseconds and
     * start with the value [start] and finish with the value [end]. The specific
     * animator used will affect the order that these values are used in.
     * (An example of this is [ReversibleAnimator] which will flip them)
     *
     * @param start The value to start the animation at
     * @param end The value to end the animation at
     * @param duration The duration that the animation will span (in milliseconds)
     */
    fun start(start: Float, end: Float, duration: Long)

    /**
     * get Returns the current value of the animation this is between
     * the start value and the end value of the animation
     *
     * @return
     */
    fun get(): Float

    /**
     * getValue Allows Kotlin to use the animator as a "Delegate" allowing for
     * the syntax of
     * ```kotlin
     * val animator = SomeAnimator()
     * val animationValue by animator
     *
     * // animationValue will be a float and call the get method whenever its used
     *```
     * @param thisRef
     * @param property
     * @return
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return get()
    }
}