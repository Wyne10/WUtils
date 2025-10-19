package me.wyne.wutils.common.kotlin.vector

import me.wyne.wutils.common.vector.VectorUtils
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

val Vector.ZERO: Vector
    get() = Vector()

fun Vector.getMin(vector: Vector): Vector =
    VectorUtils.getMin(this, vector)

fun Vector.getMax(vector: Vector): Vector =
    VectorUtils.getMax(this, vector)

fun Vector.isEmpty() =
    VectorUtils.isEmpty(this)

fun Vector.addRelative(horizontal: Double, vertical: Double, face: BlockFace) =
    VectorUtils.addRelative(this, horizontal, vertical, face)

fun Vector.addRelative(width: Double, height: Double, depth: Double, face: BlockFace) =
    VectorUtils.addRelative(this, width, height, depth, face)

fun Vector.addRelative(relativeOffset: Vector, face: BlockFace) =
    VectorUtils.addRelative(this, relativeOffset, face)

fun Vector.addRelative(relativeOffset: Vector, forward: Vector) =
    VectorUtils.addRelative(this, relativeOffset, forward)

