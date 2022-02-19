package com.jacobtread.alto.utils.math

import org.joml.Vector3d
import org.joml.Vector3i
import org.joml.Vector3ic
import java.util.function.Predicate

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

        fun boxIterator(center: BlockPos, radius: Int, height: Int = radius): Iterable<BlockPos> {
            return boxIterator(center.sub(radius, height, radius), center.add(radius, height, radius))
        }

        fun boxIteratorWithDist(center: BlockPos, radius: Int, height: Int = radius): CenterDistIterator {
            return CenterDistIterator(center, radius, height)
        }

        fun faceIterator(origin: BlockPos, facePredicate: Predicate<Facing>? = null): FacingIterator {
            val faces: Array<Facing> = if (facePredicate is Facing.Plane) {
                facePredicate.facings()
            } else if (facePredicate != null) {
                Facing.values().filter { facePredicate.test(it) }.toTypedArray()
            } else {
                Facing.values()
            }
            return FacingIterator(origin, faces)
        }

        fun faceIterator(origin: BlockPos, faces: Collection<Facing>): FacingIterator {
            return FacingIterator(origin, faces.toTypedArray())
        }

        class FacingIterator(val origin: BlockPos, val faces: Array<Facing>) : AbstractIterator<BlockPos>() {
            private val current: BlockPos = BlockPos()
            private var index = 0
            var facing: Facing = faces[0]

            override fun computeNext() {
                if (index < faces.size) {
                    facing = faces[index]
                    origin.offset(facing, current)
                    index++
                    setNext(current)
                } else {
                    done()
                }
            }
        }

        class CenterDistIterator(val center: BlockPos, val radius: Int, val height: Int = radius) : AbstractIterator<BlockPos>() {
            private val from = center.sub(radius, height, radius)
            private val to = center.add(radius, height, radius)
            private val current = from.copy()
            private var isFirst = true

            var xDist = 0
            var yDist = 0
            var zDist = 0

            override fun computeNext() {
                if (isFirst) {
                    setNext(current)
                    isFirst = false
                } else if (current == to) {
                    done()
                } else {
                    if (current.x < to.x) {
                        xDist++

                        current.x++
                    } else if (current.y < to.y) {
                        xDist = 0
                        yDist++

                        current.x = from.x
                        current.y++
                    } else if (current.z < to.z) {
                        xDist = 0
                        yDist = 0
                        zDist++

                        current.x = from.x
                        current.y = from.y
                        current.z++
                    }
                    setNext(current)
                }
            }
        }

        class BoxIterator(val from: BlockPos, val to: BlockPos) : AbstractIterator<BlockPos>() {
            private val blockPos: BlockPos = from.copy()
            private var isFirst = true

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

        @JvmStatic
        fun boxIterator(from: BlockPos, to: BlockPos): Iterable<BlockPos> {
            from.min(to)
            to.max(from)
            return object : Iterable<BlockPos> {
                override fun iterator(): Iterator<BlockPos> = BoxIterator(from, to)
            }
        }
    }

    constructor() : this(0, 0, 0)

    constructor(x: Double, y: Double, z: Double) : this(Maths.floor(x), Maths.floor(y), Maths.floor(z))
    constructor(vector3i: Vector3i) : this(vector3i.x, vector3i.y, vector3i.z)
    constructor(vector3d: Vector3d) : this(vector3d.x, vector3d.y, vector3d.z)

   @Suppress("NOTHING_TO_INLINE")
   inline operator fun times(facing: Facing): BlockPos {
        return offset(facing)
    }

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

    fun offset(n: Int, vararg faces: Facing): BlockPos {
        val out = BlockPos()
        faces.forEach { out.offset(it, out, n) }
        return out
    }

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

    fun sub(x: Int, y: Int, z: Int, dest: BlockPos): BlockPos {
        dest.x = this.x - x
        dest.y = this.y - y
        dest.z = this.z - z
        return dest
    }

    fun add(x: Int, y: Int, z: Int, dest: BlockPos): BlockPos {
        dest.x = this.x + x
        dest.y = this.y + y
        dest.z = this.z + z
        return dest
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
     * isValidY The same as [isValidY] but only checks if the y axis is
     * valid and ignores the x and z axis
     *
     * @return Whether the y axis value is within 0 and 255
     */
    fun isValidY(): Boolean {
        return y in 0..255
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

    fun asDoubleArray(): DoubleArray {
        return doubleArrayOf(x.toDouble(), y.toDouble(), z.toDouble())
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