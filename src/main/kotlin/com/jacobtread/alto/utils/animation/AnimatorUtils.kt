package com.jacobtread.alto.utils.animation

fun repeatingAnimator(
    function: AnimatorFunction,
    start: Float,
    end: Float,
    duration: Long,
    delay: Long = 0L,
): Animator {
    val animator = ReversibleAnimator(function)
    animator.startWithDelay(start, end, duration, delay)
    return animator
}

fun animator(
    function: AnimatorFunction,
    start: Float,
    end: Float,
    duration: Long,
    delay: Long = 0L,
): Animator {
    val animator = SimpleAnimator(function)
    animator.startWithDelay(start, end, duration, delay)
    return animator
}