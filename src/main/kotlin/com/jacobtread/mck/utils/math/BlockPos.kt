package com.jacobtread.mck.utils.math

import org.joml.Vector3i
import org.joml.Vector3ic

class BlockPos(x: Int, y: Int, z: Int) : Vector3i(x, y, z) {

    companion object {
        val ORIGIN = BlockPos(0, 0, 0)
        val NULL = BlockPos(-1, -1, -1)

        private val NUM_X_BITS: Int = 1 + Maths.logBase2(Maths.roundUpToPow2(30000000))
        private val NUM_Z_BITS = NUM_X_BITS
        private val NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS
        private val Y_MASK = (1L shl NUM_Y_BITS) - 1L
        private val Y_SHIFT = NUM_Z_BITS
        private val X_SHIFT = Y_SHIFT + NUM_Y_BITS
        private val Z_MASK = (1L shl NUM_Z_BITS) - 1L
        private val X_MASK = (1L shl NUM_X_BITS) - 1L

        fun Long.toBlockPos(): BlockPos {
            val x = ((this shl (64 - X_SHIFT - NUM_X_BITS)) shr (64 - NUM_X_BITS))
            val y = ((this shl (64 - Y_SHIFT - NUM_Y_BITS)) shr (64 - NUM_Y_BITS))
            val z = ((this shl (64 - NUM_Z_BITS)) shr (64 - NUM_Z_BITS))
            return BlockPos(x.toInt(), y.toInt(), z.toInt())
        }
    }

    constructor(x: Double, y: Double, z: Double) : this(floor(x), floor(y), floor(z))

    override fun add(x: Int, y: Int, z: Int): BlockPos {
        if (x == 0 && y == 0 && z == 0) return this
        return BlockPos(this.x + x, this.y + y, this.z + z)
    }

    override fun add(v: Vector3ic): BlockPos {
        if (v.x() == 0 && v.y() == 0 && v.z() == 0) return this
        val out = BlockPos(x, y, z)
        out.add(v, out)
        return out
    }

    override fun sub(v: Vector3ic): Vector3i {
        if (v.x() == 0 && v.y() == 0 && v.z() == 0) return this
        val out = BlockPos(x, y, z)
        out.sub(v, out)
        return out
    }

    fun distanceSqToCenter(x: Double, y: Double, z: Double): Double {
        val dx = this.x + 0.5 - x
        val dy = this.y + 0.5 - y
        val dz = this.z + 0.5 - z
        return dx * dx + dy * dy + dz * dz
    }

    fun distanceSquaredD(x: Double, y: Double, z: Double): Double {
        val dx = this.x - x
        val dy = this.y - y
        val dz = this.z - z
        return dx * dx + dy * dy + dz * dz
    }


    fun asLong(): Long {
        return ((x.toLong() and X_MASK) shl X_SHIFT) or
                ((y.toLong() and Y_MASK) shl Y_SHIFT) or
                (z.toLong() and Z_MASK);
    }

    operator fun component1(): Int = x
    operator fun component2(): Int = y
    operator fun component3(): Int = z
}