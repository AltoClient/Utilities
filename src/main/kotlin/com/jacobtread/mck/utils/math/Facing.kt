package com.jacobtread.mck.utils.math

import org.joml.Vector3f
import org.joml.Vector3i
import java.util.*
import java.util.function.Predicate
import kotlin.math.abs

enum class Facing(
    val index: Int,
    val horizontalIndex: Int,
    private val oppositeIndex: Int,
    val facingName: String,
    val axisDirection: AxisDirection,
    val axis: Axis,
    val direction: Vector3i
) {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, Vector3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, Vector3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, Vector3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, Vector3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, Vector3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, Vector3i(1, 0, 0));

    companion object {
        val VALUES = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST)
        val HORIZONTALS = arrayOf(NORTH, SOUTH, WEST, EAST)
        val NAME_LOOKUP = mapOf(
            "down" to DOWN,
            "up" to UP,
            "north" to NORTH,
            "south" to SOUTH,
            "west" to WEST,
            "east" to EAST
        )

        @JvmStatic
        fun byAxis(direction: AxisDirection, axis: Axis): Facing {
            for (value in VALUES) {
                if (value.axisDirection == direction && value.axis == axis) {
                    return value
                }
            }
            throw IllegalArgumentException("No such direction $direction, $axis")
        }

        @JvmStatic
        fun byVector(vector3f: Vector3f): Facing {
            var facing = NORTH
            var min = Float.MIN_VALUE
            for (value in VALUES) {
                val dirVec = value.direction
                val sq = vector3f.x * dirVec.x + vector3f.y * dirVec.y + vector3f.z * dirVec.z
                if (sq > min) {
                    min = sq
                    facing = value
                }
            }
            return facing
        }

        /**
         * random Returns a random facing value
         *
         * @param random The random to use
         * @return The facing value
         */
        @JvmStatic
        fun random(random: Random): Facing = VALUES[random.nextInt(VALUES.size)]

        @JvmStatic
        fun getHorizontal(index: Int): Facing = HORIZONTALS[abs(index)]

        @JvmStatic
        fun getFront(index: Int): Facing = VALUES[abs(index % VALUES.size)]

        /**
         * byAngle Converts the provided angle to its
         * facing value and returns it
         *
         * @param angle The angle of the face
         * @return The facing value at that angle
         */
        @JvmStatic
        fun byAngle(angle: Float): Facing = getHorizontal(floor(angle / 90f + 0.5f) and 3)

        /**
         * byName Searches the [NAME_LOOKUP] for the provided
         * name and returns the respective facing or null
         * if the name was null or did not exist in the lookup
         *
         * @param name The name to search for
         * @return The facing found or null if name is not valid
         */
        @JvmStatic
        fun byName(name: String?): Facing? = if (name == null) null else NAME_LOOKUP[name.lowercase()]

    }

    val frontOffsetX: Int get() = axisOrZero(Axis.X)
    val frontOffsetY: Int get() = axisOrZero(Axis.Y)
    val frontOffsetZ: Int get() = axisOrZero(Axis.Z)

    val opposite: Facing get() = VALUES[oppositeIndex]

    fun rotateAround(axis: Axis): Facing {
        return when (axis) {
            Axis.X -> if (this != WEST && this != EAST) {
                when (this) {
                    NORTH -> DOWN
                    SOUTH -> UP
                    UP -> NORTH
                    DOWN -> SOUTH
                    else -> throw IllegalStateException("Unable to get X-rotated of $this")
                }
            } else this
            Axis.Y -> if (this != UP && this != DOWN) rotate() else this
            Axis.Z -> if (this != NORTH && this != SOUTH) {
                when (this) {
                    EAST -> DOWN
                    WEST -> UP
                    UP -> EAST
                    DOWN -> WEST
                    else -> throw IllegalStateException("Unable to get Z-rotated of $this")
                }
            } else this
        }
    }

    /**
     * rotate Rotates the facing around the Y axis
     * clockwise
     * NORTH, EAST, SOUTH, WEST
     *
     * @return The facing rotated clock-wise
     */
    fun rotate(): Facing {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
            else -> throw IllegalStateException("Unable to get clockwise facing of $this")
        }
    }

    /**
     * rotateReverse Rotates this face counter clock-wise
     * and returns the resulted facing e.g.
     * NORTH, WEST, SOUTH, EAST
     *
     * @return The facing rotated counter clock-wise
     */
    fun rotateReverse(): Facing {
        return when (this) {
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
            else -> throw IllegalStateException("Unable to get counter clockwise facing of $this")
        }
    }

    fun axisOrZero(axis: Axis): Int {
        return if (this.axis == axis) axisDirection.offset else 0
    }

    override fun toString(): String {
        return facingName
    }

    enum class Axis(val axisName: String, val plane: Plane) : Predicate<Facing?> {
        X("x", Plane.HORIZONTAL),
        Y("y", Plane.VERTICAL),
        Z("z", Plane.HORIZONTAL);

        companion object {
            private val NAME_LOOKUP = mapOf(
                "x" to X,
                "y" to Y,
                "z" to Z
            )

            @JvmStatic
            fun byName(name: String?): Axis? = if (name == null) null else NAME_LOOKUP[name.lowercase()]
        }

        val isVertical: Boolean get() = plane == Plane.VERTICAL
        val isHorizontal: Boolean get() = plane == Plane.HORIZONTAL

        override fun test(t: Facing?): Boolean {
            return t != null && t.axis == this
        }

        override fun toString(): String {
            return axisName
        }
    }

    enum class AxisDirection(val offset: Int, val description: String) {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        override fun toString(): String {
            return description
        }
    }

    enum class Plane : Predicate<Facing?>, Iterable<Facing> {
        HORIZONTAL {
            override fun facings() = arrayOf(NORTH, EAST, SOUTH, WEST)
        },
        VERTICAL {
            override fun facings() = arrayOf(UP, DOWN)
        };

        abstract fun facings(): Array<Facing>

        override fun iterator(): Iterator<Facing> = facings().iterator()

        override fun test(t: Facing?): Boolean = t != null && t.axis.plane == this

        fun random(random: Random): Facing {
            val facings = facings()
            return facings[random.nextInt(facings.size)]
        }

    }
}