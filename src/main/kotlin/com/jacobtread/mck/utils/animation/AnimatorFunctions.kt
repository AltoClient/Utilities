package com.jacobtread.mck.utils.animation

import com.jacobtread.mck.utils.math.Maths
import kotlin.math.pow

fun easeOutQuart(): AnimatorFunction = AnimatorFunction { progress ->
    // 1 - pow(1 - x, 4);
    1f - (1f - progress).pow(4f)
}

fun easeInOutSine(): AnimatorFunction = AnimatorFunction { progress ->
    // -(cos(PI * x) - 1) / 2;
    -(Maths.cos(Maths.PI * progress) - 1f) / 2f
}