package com.jacobtread.alto.utils.math

import org.joml.Vector3d
import org.joml.Vector3i
import org.joml.Vector3ic

open class BlockPos(x: Int, y: Int, z: Int) : Vector3i(x, y, z) {

    companion object {
        @JvmField
        val ORIGIN = BlockPos()

        @JvmField
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

        @JvmStatic
        fun boxIterator(from: BlockPos, to: BlockPos): Iterable<BlockPos> {
            from.min(to)
            to.max(from)
            return object : Iterable<BlockPos> {
                override fun iterator(): Iterator<BlockPos> {
                    return object : AbstractIterator<BlockPos>() {
                        var blockPos: BlockPos = from.copy()
                        var isFirst = true

                        override fun computeNext() {
                            if (isFirst) {
                                setNext(blockPos)
                                isFirst = false
                            } else if (blockPos == to) {
                                done()
                            } else {
                                if (blockPos.x < to.x) {
                                    blockPos.x++
                                } else if (blockPos.y < to.y) {
                                    blockPos.x = from.x
                                    blockPos.y++
                                } else if (blockPos.z < to.z) {
                                    blockPos.x = from.x
                                    blockPos.y = from.y
                                    blockPos.z++
                                }
                                setNext(blockPos)
                            }
                        }
                    }
                }
            }
        }
    }

    constructor() : this(0, 0, 0)

    constructor(x: Double, y: Double, z: Double) : this(Maths.floor(x), Maths.floor(y), Maths.floor(z))
    constructor(vector3i: Vector3i) : this(vector3i.x, vector3i.y, vector3i.z)
    constructor(vector3d: Vector3d) : this(vector3d.x, vector3d.y, vector3d.z)

    @JvmOverloads
    fun up(n: Int = 1): BlockPos = offset(Facing.UP, n)

    @JvmOverloads
    fun down(n: Int = 1): BlockPos = offset(Facing.DOWN, n)

    @JvmOverloads
    fun north(n: Int = 1): BlockPos = offset(Facing.NORTH, n)

    @JvmOverloads
    fun south(n: Int = 1): BlockPos = offset(Facing.SOUTH, n)

    @JvmOverloads
    fun east(n: Int = 1): BlockPos = offset(Facing.EAST, n)

    @JvmOverloads
    fun west(n: Int = 1): BlockPos = offset(Facing.WEST, n)

    @JvmOverloads
    fun offset(facing: Facing, n: Int = 1): BlockPos {
        return if (n == 0) this else BlockPos(
            x + facing.frontOffsetX * n,
            y + facing.frontOffsetY * n,
            z + facing.frontOffsetZ * n,
        )
    }

    @JvmOverloads
    fun offset(facing: Facing, dest: BlockPos, n: Int = 1): BlockPos {
        if (n > 0) {
            dest.x = x + facing.frontOffsetX * n
            dest.y = y + facing.frontOffsetY * n
            dest.z = z + facing.frontOffsetZ * n
        }
        return this
    }

    override fun set(x: Int, y: Int, z: Int): BlockPos {
        super.set(x, y, z)
        return this
    }

    override fun add(x: Int, y: Int, z: Int): BlockPos {
        if (x == 0 && y == 0 && z == 0) return this
        return BlockPos(this.x + x, this.y + y, this.z + z)
    }

    fun add(x: Double, y: Double, z: Double): BlockPos {
        if (x == 0.0 && y == 0.0 && z == 0.0) return this
        return BlockPos(this.x + x, this.y + y, this.z + z)
    }

    override fun add(v: Vector3ic): BlockPos {
        if (v.x() == 0 && v.y() == 0 && v.z() == 0) return this
        return BlockPos(x + v.x(), y + v.y(), z + v.z())
    }

    override fun sub(v: Vector3ic): BlockPos {
        if (v.x() == 0 && v.y() == 0 && v.z() == 0) return this
        return BlockPos(x - v.x(), y - v.y(), z - v.z())
    }

    override fun sub(x: Int, y: Int, z: Int): BlockPos {
        if (x == 0 && y == 0 && z == 0) return this
        return BlockPos(this.x - x, this.y - y, this.z - z)
    }

    /**
     * isValid Checks if this block pos is a valid position
     * within the game world
     *
     * must be between y-level 0 and 255
     * must be between -30000000 and 30000000 x and z
     *
     * @return Whether this position is valid
     */
    fun isValid(): Boolean {
        return y in 0..255
                && x >= -30000000 && x < 30000000
                && z >= -30000000 && z < 30000000
    }

    /**
     * isValidXZ The same as [isValid] but only checks the x and z axis
     * and ignores the y-axis
     *
     * @return Whether the x and z axis are valid
     */
    fun isValidXZ(): Boolean {
        return x >= -30000000 && x < 30000000 && z >= -30000000 && z < 30000000
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

    fun cross(b: Vector3i): Vector3i {
        return crossProduct(b)
    }

    fun copy(): BlockPos {
        return BlockPos(this)
    }

    fun asLong(): Long {
        return ((x.toLong() and X_MASK) shl X_SHIFT) or
                ((y.toLong() and Y_MASK) shl Y_SHIFT) or
                (z.toLong() and Z_MASK);
    }

    operator fun component1(): Int = x
    operator fun component2(): Int = y
    operator fun component3(): Int = z

    override fun hashCode(): Int {
        return (y + z * 31) * 31 + x
    }

    override fun toString(): String {
        return "$x, $y, $z"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPos) return false
        return x == other.x && y == other.y && z == other.z
    }
}