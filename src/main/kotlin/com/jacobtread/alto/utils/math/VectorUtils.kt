package com.jacobtread.alto.utils.math

import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector3i

// UTIL FUNCTIONS FOR WORKING WITH VECTORS

fun Vector3d.add(vector3i: Vector3i): Vector3d {
    return add(vector3i.x.toDouble(), vector3i.y.toDouble(), vector3i.z.toDouble())
}

fun Vector3d.add(vector3i: Vector3i, dest: Vector3d): Vector3d {
    add(vector3i.x.toDouble(), vector3i.y.toDouble(), vector3i.z.toDouble(), dest)
    return dest
}

fun Vector3d.sub(vector3i: Vector3i): Vector3d {
    return sub(vector3i.x.toDouble(), vector3i.y.toDouble(), vector3i.z.toDouble())
}

fun Vector3d.sub(vector3i: Vector3i, dest: Vector3d): Vector3d {
    sub(vector3i.x.toDouble(), vector3i.y.toDouble(), vector3i.z.toDouble(), dest)
    return dest
}

fun Vector3d.isNaN(): Boolean {
    return x.isNaN() || y.isNaN() || z.isNaN()
}

private operator fun Vector3d.component1(): Double = x
private operator fun Vector3d.component2(): Double = y
private operator fun Vector3d.component3(): Double = z

private operator fun Vector3i.component1(): Int = x
private operator fun Vector3i.component2(): Int = y
private operator fun Vector3i.component3(): Int = z

private operator fun Vector3f.component1(): Float = x
private operator fun Vector3f.component2(): Float = y
private operator fun Vector3f.component3(): Float = z

