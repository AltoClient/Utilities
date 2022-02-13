package com.jacobtread.alto.utils.math

import org.joml.Vector3d
import org.joml.Vector3i
import kotlin.math.max
import kotlin.math.min

/**
 * AxisAlignedBB An Axis-Aligned Bounding Box implementation
 *
 * @constructor
 *
 * @param minX The minimum x coord of this bounding box
 * @param minY The minimum y coord of this bounding box
 * @param minZ The minimum z coord of this bounding box
 * @param maxX The maximum x coord of this bounding box
 * @param maxY The maximum y coord of this bounding box
 * @param maxZ The maximum z coord of this bounding box
 */
class AxisAlignedBB(
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
) {

    companion object {
        /**
         * ONE A simple bounding box that is one block wide and one block tall
         */
        val ONE = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    }

    val minX = min(minX, maxX)
    val minY = min(minY, maxY)
    val minZ = min(minZ, maxZ)

    val maxX = max(maxX, minX)
    val maxY = max(maxY, minY)
    val maxZ = max(maxZ, minZ)

    constructor(start: Vector3i, end: Vector3i) : this(
        start.x.toDouble(),
        start.y.toDouble(),
        start.z.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
        end.z.toDouble()
    )

    constructor(start: Vector3d, end: Vector3d) : this(
        start.x, start.y, start.z,
        end.x, end.y, end.z
    )

    constructor(other: AxisAlignedBB) : this(
        other.minX, other.minY, other.minZ,
        other.maxX, other.maxY, other.maxZ
    )

    /**
     * copy Creates a copy of this bounding box
     * and returns it
     *
     * @return The new copied bounding box
     */
    fun copy(): AxisAlignedBB = AxisAlignedBB(this)

    /**
     * add Moves this bounding box by [x], [y], [z] coords and returns a new
     * bounding box in the position. (Values less than zero will affect the
     * [minX], [minY], [minZ] and values greater than zero will affect the max
     * values [maxX] [maxY] [maxZ])
     *
     * @param x The x coord to move the bounding box by
     * @param y The y coord to move the bounding box by
     * @param z The z coord to move the bounding box by
     * @return The new axis aligned bounding box
     */
    fun add(x: Double, y: Double, z: Double): AxisAlignedBB {
        var d0 = minX
        var d1 = minY
        var d2 = minZ
        var d3 = maxX
        var d4 = maxY
        var d5 = maxZ
        if (x < 0.0) d0 += x
        else if (x > 0.0) d3 += x

        if (y < 0.0) d1 += y
        else if (y > 0.0) d4 += y

        if (z < 0.0) d2 += z
        else if (z > 0.0) d5 += z
        return AxisAlignedBB(d0, d1, d2, d3, d4, d5)
    }

    /**
     * add Wrapping method for [add] to allow the use of [Vector3d]
     *
     * @see add The underlying method
     * @param vector3d The vector3 to move the bounding box by
     * @return The new moved bounding box
     */
    fun add(vector3d: Vector3d): AxisAlignedBB = add(vector3d.x, vector3d.y, vector3d.z)

    /**
     * expand Expands both the minimum and maximum values by the
     * provided [x], [y], [z] values
     *
     * @param x The x value to expand by
     * @param y The y value to expand by
     * @param z The z value to expand by
     * @return The new expanded bounding box
     */
    fun expand(x: Double, y: Double, z: Double): AxisAlignedBB {
        return AxisAlignedBB(
            minX - x, minY - y, minZ - z,
            maxX + x, maxY + y, maxZ + z
        )
    }

    /**
     * expand Wrapping method for [expand] to allow the use of
     * a [Vector3d] instead of provided x,y,z values
     *
     * @param vector3d The vector to expand by
     * @return The new expanded bounding box
     */
    fun expand(vector3d: Vector3d): AxisAlignedBB = expand(vector3d.x, vector3d.y, vector3d.z)

    /**
     * expand Expands all axis of the bounding box by the
     * provided [amount] (Wrapping method of [expand])
     *
     * @param amount The amount to expand by
     * @return The new expanded bounding box
     */
    fun expand(amount: Double): AxisAlignedBB = expand(amount, amount, amount)

    /**
     * contract Contracts the bounding box size by the provided
     * [x], [y], [z] offsets amount. This insets the bounding box
     * (This insets both the min and max values of this box)
     *
     * @param x The x amount to indent by
     * @param y The y amount to indent by
     * @param z The z amount to indent by
     * @return The new contracted bounding box
     */
    fun contract(x: Double, y: Double, z: Double): AxisAlignedBB {
        return AxisAlignedBB(
            minX + x, minY + y, minZ + z,
            maxX - x, maxY - y, maxZ - z
        )
    }

    /**
     * combine Combines the two bounding boxes using the lowest
     * min and highest max values of each bounding boxes
     * (Creates a larger bounding box that spans over both)
     *
     * @param other The bounding box to combine with
     * @return The new combined bounding box
     */
    fun combine(other: AxisAlignedBB): AxisAlignedBB {
        return AxisAlignedBB(
            min(minX, other.minX),
            min(minY, other.minY),
            min(minZ, other.minZ),
            max(maxX, other.maxX),
            max(maxY, other.maxY),
            max(maxZ, other.maxZ),
        )
    }

    /**
     * offset Moves the entire bounding box over by the
     * provided [x], [y], [z] coords
     *
     * @param x The amount to move on the x-axis
     * @param y The amount to move on the y-axis
     * @param z The amount to move on the z-axis
     * @return The new moved bounding box
     */
    fun offset(x: Double, y: Double, z: Double): AxisAlignedBB {
        return AxisAlignedBB(
            minX + x, minY + y, minZ + z,
            maxX + x, maxY + y, maxZ + z
        )
    }

    /**
     * offset Moves the entire bounding box over by the
     * provided x,y,z coords of the provided [vector]
     *
     * @param vector The vector to offset by
     * @return The new moved bounding box
     */
    fun offset(vector: Vector3i): AxisAlignedBB {
        return AxisAlignedBB(
            minX + vector.x, minY + vector.y, minZ + vector.z,
            maxX + vector.x, maxY + vector.y, maxZ + vector.z
        )
    }

    /**
     * offset Moves the entire bounding box over by the
     * provided x,y,z coords of the provided [vector]
     *
     * @param vector The vector to offset by
     * @return The new moved bounding box
     */
    fun offset(vector: Vector3d): AxisAlignedBB {
        return AxisAlignedBB(
            minX + vector.x, minY + vector.y, minZ + vector.z,
            maxX + vector.x, maxY + vector.y, maxZ + vector.z
        )
    }

    /**
     * calculateXOverlap Checks if the [other] bounding box overlaps with this
     * bounding box and if it does it calculate the overlap position on the x-axis
     * or if it doesn't overlap [default] is returned instead
     *
     * @param other The other bounding box to check overlaping
     * @param default The default value to return
     * @return The x-axis overlap position or [default]
     */
    fun calculateXOverlap(other: AxisAlignedBB, default: Double): Double {
        if (other.maxY > minY && other.minY < maxY
            && other.maxZ > minZ && other.minZ < maxZ
        ) {
            if (default > 0.0 && other.maxX <= minX) {
                val diff = minX - other.maxX
                if (diff < default) return diff
            } else if (default < 0.0 && other.minX >= maxX) {
                val diff = maxX - other.minX
                if (diff > default) return diff
            }
        }
        return default
    }

    /**
     * calculateYOverlap Checks if the [other] bounding box overlaps with this
     * bounding box and if it does it calculate the overlap position on the y-axis
     * or if it doesn't overlap [default] is returned instead
     *
     * @param other The other bounding box to check overlaping
     * @param default The default value to return
     * @return The y-axis overlap position or [default]
     */
    fun calculateYOverlap(other: AxisAlignedBB, default: Double): Double {
        if (other.maxX > minX && other.minX < maxX
            && other.maxZ > minZ && other.minZ < maxZ
        ) {
            if (default > 0.0 && other.maxY <= minY) {
                val diff = minY - other.maxY
                if (diff < default) return diff
            } else if (default < 0.0 && other.minY >= maxY) {
                val diff = maxY - other.minY
                if (diff > default) return diff
            }
        }
        return default
    }

    /**
     * calculateXOverlap Checks if the [other] bounding box overlaps with this
     * bounding box and if it does it calculate the overlap position on the x-axis
     * or if it doesn't overlap [default] is returned instead
     *
     * @param other The other bounding box to check overlaping
     * @param default The default value to return
     * @return The x-axis overlap position or [default]
     */
    fun calculateZOverlap(other: AxisAlignedBB, default: Double): Double {
        if (other.maxX > minX && other.minX < maxX
            && other.maxY > minY && other.minY < maxY
        ) {
            if (default > 0.0 && other.maxZ <= minZ) {
                val diff = minZ - other.maxZ
                if (diff < default) return diff
            } else if (default < 0.0 && other.minZ >= maxZ) {
                val diff = maxZ - other.minZ
                if (diff > default) return diff
            }
        }
        return default
    }

    /**
     * intersects Checks if the [other] bounding box intersects
     * with this bounding box
     *
     * @param other The other bounding box
     * @return Whether the bounding box intersects
     */
    fun intersects(other: AxisAlignedBB): Boolean {
        return other.maxX > minX && other.minX < maxX
                && other.maxY > minY && other.minY < maxY
                && other.maxZ > minZ && other.minZ < maxZ
    }

    /**
     * contains Checks whether the provided [vector3d] [Vector3d] is
     * inside this bounding box
     *
     * @param vector3d The vector to check if is inside
     * @return Whether the vector is inside the bounding box
     */
    fun contains(vector3d: Vector3d?): Boolean {
        return vector3d != null && vector3d.x > minX && vector3d.x < maxX
                && vector3d.y > minY && vector3d.y < maxY
                && vector3d.z > minZ && vector3d.z < maxZ
    }

    /**
     * contains Checks whether the provided [vector3d] [Vector3d] is
     * inside this bounding box (Only checks if the vector is inside
     * on the Y and Z axes)
     *
     * @param vector3d The vector to check if is inside
     * @return Whether the vector is inside the bounding box
     */
    fun containsYZ(vector3d: Vector3d?): Boolean {
        return vector3d != null && vector3d.y > minY && vector3d.y < maxY
                && vector3d.z > minZ && vector3d.z < maxZ
    }

    /**
     * contains Checks whether the provided [vector3d] [Vector3d] is
     * inside this bounding box (Only checks if the vector is inside
     * on the X and Z axes)
     *
     * @param vector3d The vector to check if is inside
     * @return Whether the vector is inside the bounding box
     */
    fun containsXZ(vector3d: Vector3d?): Boolean {
        return vector3d != null && vector3d.x > minX && vector3d.x < maxX
                && vector3d.z > minZ && vector3d.z < maxZ
    }

    /**
     * contains Checks whether the provided [vector3d] [Vector3d] is
     * inside this bounding box (Only checks if the vector is inside
     * on the X and Y axes)
     *
     * @param vector3d The vector to check if is inside
     * @return Whether the vector is inside the bounding box
     */
    fun containsXY(vector3d: Vector3d?): Boolean {
        return vector3d != null && vector3d.x > minX && vector3d.x < maxX
                && vector3d.y > minY && vector3d.y < maxY
    }

    /**
     * getAverageEdgeLength Returns the average length
     * of all the different axis
     *
     * @return The average length of the axis
     */
    fun getAverageEdgeLength(): Double {
        return ((maxX - minX) + (maxY - minY) + (maxZ - minZ)) / 3.0
    }

    /**
     * isInvalid Checks if this bounding box is invalid
     * (contains a NaN value for one of its points)
     *
     * @return Whether this bounding box is invalid
     */
    fun isInvalid(): Boolean {
        return minX.isNaN() || minY.isNaN() || minZ.isNaN() || maxX.isNaN() || maxY.isNaN() || maxZ.isNaN()
    }

    /**
     * rayTrace Draws a line between the point [start] -> [end] and calculates
     * the point if anywhere that lines first intersects with this bounding box
     * will return the [Facing] and [Vector3d] point of intersection if the point
     * does intersect with the bounding box or null if they did not intersect
     *
     * @param start The start point of the ray trace line
     * @param end The end point of the ray trace line
     * @return The [Vector3d] and [Facing] of the intersection or null if no intersection
     */
    fun rayTrace(start: Vector3d, end: Vector3d): Pair<Vector3d, Facing>? {
        var a = Maths.getIntermediateWithXValue(start, end, minX)
        var a2 = Maths.getIntermediateWithXValue(start, end, maxX)
        var b = Maths.getIntermediateWithXValue(start, end, minY)
        var b2 = Maths.getIntermediateWithXValue(start, end, maxY)
        var c = Maths.getIntermediateWithXValue(start, end, minZ)
        var c2 = Maths.getIntermediateWithXValue(start, end, maxZ)
        if (!containsYZ(a)) a = null
        if (!containsYZ(a2)) a2 = null
        if (!containsXZ(b)) b = null
        if (!containsXZ(b2)) b2 = null
        if (!containsXY(c)) c = null
        if (!containsXY(c2)) c2 = null
        val shortest: Vector3d = getShortest(start, a, a2, b, b2, c, c2) ?: return null
        val facing = if (shortest === a) {
            Facing.WEST
        } else if (shortest === a2) {
            Facing.EAST
        } else if (shortest === b) {
            Facing.DOWN
        } else if (shortest === b2) {
            Facing.UP
        } else if (shortest === c) {
            Facing.NORTH
        } else {
            Facing.SOUTH
        }
        return shortest to facing
    }

    /**
     * getShortest Finds the vector with the shortest distance to [start]
     * out of the array of vectors provided as [vectors]
     * (Null vectors are ignored and considered longest)
     *
     * @param start The vector to compare distance to
     * @param vectors The vectors to compare
     * @return The shortest vector or null
     */
    private fun getShortest(start: Vector3d, vararg vectors: Vector3d?): Vector3d? {
        var shortest: Vector3d? = null
        vectors.forEach {
            if (it != null && (shortest == null
                        || start.distanceSquared(it) < start.distanceSquared(shortest))
            ) {
                shortest = it
            }
        }
        return shortest
    }

    override fun toString(): String = "Bounds($minX, $minY, $minZ, $maxX, $maxY, $maxZ)"

}