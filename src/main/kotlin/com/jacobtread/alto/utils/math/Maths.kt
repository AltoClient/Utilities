package com.jacobtread.alto.utils.math

import org.joml.RoundingMode
import org.joml.Vector3d
import org.joml.Vector3i
import java.util.*
import kotlin.math.asin
import kotlin.math.roundToInt
import kotlin.math.sin

fun Vector3i.crossProduct(v: Vector3i): Vector3i {
    return Vector3i(
        Math.fma(y.toDouble(), v.z().toDouble(), (-z * v.y()).toDouble()),
        Math.fma(z.toDouble(), v.x().toDouble(), (-x * v.z()).toDouble()),
        Math.fma(x.toDouble(), v.y().toDouble(), (-y * v.x()).toDouble()),
        RoundingMode.FLOOR
    )
}


object Maths {
    const val PI: Float = Math.PI.toFloat()
    const val PI_2: Float = PI * 2f
    const val PI_D2: Float = PI / 2f
    const val DEG_2_RAD: Float = 0.017453292f

    private val multiplyDeBruijnBitPosition: IntArray = intArrayOf(
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20,
        15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19,
        16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    )
    private val SIN_TABLE_FAST = FloatArray(4096)
    private val SIN_TABLE = FloatArray(65536)
    const val DEG_2_RAD_DOUBLE = 0.017453292519943295
    private val RAD_TO_INDEX: Float = roundToFloat(651.8986469044033)
    var fastMath = false
    private val ASIN_TABLE = FloatArray(65536)

    init {
        for (i in 0..65535) {
            SIN_TABLE[i] = sin(i.toDouble() * Math.PI * 2.0 / 65536.0).toFloat()
        }
        for (j in SIN_TABLE_FAST.indices) {
            SIN_TABLE_FAST[j] = roundToFloat(sin(j.toDouble() * Math.PI * 2.0 / 4096.0))
        }
        for (i in 0..65535) {
            ASIN_TABLE[i] = asin(i.toDouble() / 32767.5 - 1.0).toFloat()
        }

        for (j in -1..1) {
            ASIN_TABLE[((j.toDouble() + 1.0) * 32767.5).toInt() and 65535] = asin(j.toDouble()).toFloat()
        }
    }

    @JvmStatic
    fun asin(value: Float): Float {
        return ASIN_TABLE[((value + 1.0) * 32767.5).toInt() and 65535]
    }

    @JvmStatic
    fun acos(value: Float): Float {
        return Math.PI.toFloat() / 2f - ASIN_TABLE[((value + 1.0) * 32767.5).toInt() and 65535]
    }

    @JvmStatic
    fun sqrt(value: Double): Float {
        return kotlin.math.sqrt(value).toFloat()
    }

    fun roundToFloat(d: Double): Float {
        return ((d * 1.0E8).roundToInt().toDouble() / 1.0E8).toFloat()
    }

    fun sinFast(value: Float): Float {
        return SIN_TABLE_FAST[(value * RAD_TO_INDEX).toInt() and 4095]
    }

    fun cosFast(value: Float): Float {
        return SIN_TABLE_FAST[(value * RAD_TO_INDEX + 1024f).toInt() and 4095]
    }

    @JvmStatic
    fun floor(value: Double): Int {
        val i = value.toInt()
        return if (value < i.toDouble()) i - 1 else i
    }

    @JvmStatic
    fun floor(value: Float): Int {
        val i = value.toInt()
        return if (value < i.toFloat()) i - 1 else i
    }

    @JvmStatic
    fun floorD2L(value: Double): Long {
        val i = value.toLong()
        return if (value < i.toDouble()) i - 1L else i
    }

    @JvmStatic
    fun ceil(value: Float): Int {
        val i = value.toInt()
        return if (value > i.toFloat()) i + 1 else i
    }

    @JvmStatic
    fun ceil(value: Double): Int {
        val i = value.toInt()
        return if (value > i.toDouble()) i + 1 else i
    }

    @JvmStatic
    fun sin(value: Float): Float {
        return if (fastMath) SIN_TABLE_FAST[(value * RAD_TO_INDEX).toInt() and 4095] else SIN_TABLE[(value * 10430.378f).toInt() and 65535]
    }

    @JvmStatic
    fun abs(value: Float): Float {
        return if (value >= 0.0f) value else -value
    }

    @JvmStatic
    fun abs(value: Int): Int {
        return if (value >= 0) value else -value
    }

    @JvmStatic
    fun cos(value: Float): Float {
        return if (fastMath) SIN_TABLE_FAST[(value * RAD_TO_INDEX + 1024.0f).toInt() and 4095] else SIN_TABLE[(value * 10430.378f + 16384.0f).toInt() and 65535]
    }

    @JvmStatic
    fun toDeg(angle: Float): Float {
        return angle * 180.0f / PI
    }

    @JvmStatic
    fun toRad(angle: Float): Float {
        return angle / 180.0f * PI
    }


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

    @JvmStatic
    fun wrapAbs(a: Int, b: Int): Int {
        return (a % b + b) % b
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

    fun roundUpToMultiple(value: Int, divisor: Int): Int {
        return ceilDiv(value, divisor) * divisor
    }

    fun ceilDiv(a: Int, b: Int): Int {
        return -Math.floorDiv(-a, b)
    }

    @JvmStatic
    fun wrapAngle(value: Float): Float {
        var v = value % 360f
        if (v >= 180f) v -= 360f
        if (v < -180f) v += 360f
        return v
    }

    @JvmStatic
    fun wrapAngle(value: Double): Double {
        var v = value % 360.0
        if (v >= 180.0) v -= 360.0
        if (v < -180.0) v += 360.0
        return v
    }

    @JvmStatic
    fun getIntermediateWithYValue(a: Vector3d, vec: Vector3d, y: Double): Vector3d? {
        val d0: Double = vec.x - a.x
        val d1: Double = vec.y - a.y
        val d2: Double = vec.z - a.z
        return if (d1 * d1 < 1.0000000116860974E-7) {
            null
        } else {
            val d3: Double = (y - a.y) / d1
            if (d3 in 0.0..1.0) Vector3d(
                a.x + d0 * d3,
                a.y + d1 * d3,
                a.z + d2 * d3
            ) else null
        }
    }

    @JvmStatic
    fun getIntermediateWithXValue(a: Vector3d, vec: Vector3d, x: Double): Vector3d? {
        val d0: Double = vec.x - a.x
        val d1: Double = vec.y - a.y
        val d2: Double = vec.z - a.z
        return if (d0 * d0 < 1.0000000116860974E-7) {
            null
        } else {
            val d3: Double = (x - a.x) / d0
            if (d3 in 0.0..1.0) Vector3d(
                a.x + d0 * d3,
                a.y + d1 * d3,
                a.z + d2 * d3
            ) else null
        }
    }

    @JvmStatic
    fun cross(a: Vector3i, b: Vector3i) = a.crossProduct(b)

    @JvmStatic
    fun random(random: Random, min: Int, max: Int): Int {
        return random.nextInt(max - min) + min
    }

    @JvmStatic
    fun random(random: Random, min: Double, max: Double): Double {
        return (random.nextDouble() * (max - min)) + min
    }

    @JvmStatic
    fun random(random: Random, min: Float, max: Float): Float {
        return (random.nextFloat() * (max - min)) + min
    }

    @JvmStatic
    fun rotatePitch(a: Vector3d, pitch: Float) {
        val f: Float = cos(pitch)
        val f1: Float = sin(pitch)
        val d1: Double = a.y * f.toDouble() + a.z * f1.toDouble()
        val d2: Double = a.z * f.toDouble() - a.y * f1.toDouble()
        a.y = d1
        a.z = d2
    }

    @JvmStatic
    fun rotateYaw(a: Vector3d, yaw: Float) {
        val f: Float = cos(yaw)
        val f1: Float = sin(yaw)
        val d0: Double = a.x * f.toDouble() + a.z * f1.toDouble()
        val d2: Double = a.z * f.toDouble() - a.x * f1.toDouble()
        a.x = d0
        a.z = d2
    }

    @JvmStatic
    fun getIntermediateWithZValue(a: Vector3d, vec: Vector3d, z: Double): Vector3d? {
        val d0: Double = vec.x - a.x
        val d1: Double = vec.y - a.y
        val d2: Double = vec.z - a.z
        return if (d2 * d2 < 1.0000000116860974E-7) {
            null
        } else {
            val d3: Double = (z - a.z) / d2
            if (d3 in 0.0..1.0) Vector3d(
                a.x + d0 * d3,
                a.y + d1 * d3,
                a.z + d2 * d3
            ) else null
        }
    }

}