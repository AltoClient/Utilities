package com.jacobtread.mck.utils.math

import org.joml.RoundingMode
import org.joml.Vector3i

fun floor(value: Double): Int {
    val i = value.toInt()
    return if (value < i.toDouble()) i - 1 else i
}

fun floor(value: Float): Int {
    val i = value.toInt()
    return if (value < i.toFloat()) i - 1 else i
}

fun Vector3i.crossProduct(v: Vector3i): Vector3i {
    return Vector3i(
        Math.fma(y.toDouble(), v.z().toDouble(), (-z * v.y()).toDouble()),
        Math.fma(z.toDouble(), v.x().toDouble(), (-x * v.z()).toDouble()),
        Math.fma(x.toDouble(), v.y().toDouble(), (-y * v.x()).toDouble()),
        RoundingMode.FLOOR
    )
}


object Maths {
    private val multiplyDeBruijnBitPosition: IntArray = intArrayOf(
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20,
        15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19,
        16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    )

    @JvmStatic
    fun roundUpToPow2(value: Int): Int {
        var i = value - 1
        i = i or (i shr 1)
        i = i or (i shr 2)
        i = i or (i shr 4)
        i = i or (i shr 8)
        i = i or (i shr 16)
        return i + 1
    }

    private fun logBase2DeBruijn(value: Int): Int {
        val a = if (isPowerOfTwo(value)) value else roundUpToPow2(value)
        return multiplyDeBruijnBitPosition[((a * 125613361L) shr 27).toInt() and 31]
    }

    private fun isPowerOfTwo(value: Int): Boolean {
        return value != 0 && (value and (value - 1)) == 0
    }

    @JvmStatic
    fun logBase2(value: Int): Int {
        return logBase2DeBruijn(value) - if (isPowerOfTwo(value)) 0 else 1
    }


}