package me.wyne.wutils.common.kotlin.location

import me.wyne.wutils.common.location.LocationUtils
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

fun locationOf(world: World, vector: Vector) =
    LocationUtils.of(world, vector)

fun locationOf(world: World, vector: Vector, direction: Vector) =
    LocationUtils.of(world, vector, direction)

fun Location.addRelative(horizontal: Double, vertical: Double, face: BlockFace) =
    LocationUtils.addRelative(this, horizontal, vertical, face)

fun Location.addRelative(width: Double, height: Double, depth: Double, face: BlockFace) =
    LocationUtils.addRelative(this, width, height, depth, face)

fun Location.addRelative(relativeOffset: Vector, face: BlockFace) =
    LocationUtils.addRelative(this, relativeOffset, face)

fun Location.addRelative(relativeOffset: Vector, forward: Vector) =
    LocationUtils.addRelative(this, relativeOffset, forward)

fun Location.addRelative(relativeOffset: Vector) =
    LocationUtils.addRelative(this, relativeOffset)

