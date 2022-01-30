package com.jacobtread.alto.utils.math

import org.joml.Vector3d
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