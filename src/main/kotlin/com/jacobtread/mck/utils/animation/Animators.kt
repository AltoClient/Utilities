package com.jacobtread.mck.utils.animation

import kotlin.reflect.KProperty

class Animators(
    val animators: HashMap<String, Animator>,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        val name = property.name
        return animators[name]?.get() ?: throw IllegalStateException("Unknown animator '$name'")
    }
}

abstract class CreatingAnimators {
    val animators = HashMap<String, Animator>()
    var delay = 0L

    abstract fun createAnimator(): Animator

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        val name = property.name
        return animators.computeIfAbsent(name) { createAnimator() }.get()
    }

    fun withDelay(delay: Long): CreatingAnimators {
        this.delay = delay
        return this
    }
}