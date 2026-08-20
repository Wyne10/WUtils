package me.wyne.wutils.common.kotlin.world

import me.wyne.wutils.common.world.WorldUtils
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

fun World.getHighestBlockAtAsync(x: Int, z: Int) =
    WorldUtils.getHighestBlockAtAsync(this, x, z)

fun World.getHighestLocationAtAsync(x: Int, z: Int) =
    WorldUtils.getHighestLocationAtAsync(this, x, z)

fun World.getHighestBlockAtAsync(x: Double, z: Double) =
    WorldUtils.getHighestBlockAtAsync(this, x, z)

fun World.getHighestLocationAtAsync(x: Double, z: Double) =
    WorldUtils.getHighestLocationAtAsync(this, x, z)

fun World.getHighestBlockAtAsync(vector: Vector) =
    WorldUtils.getHighestBlockAtAsync(this, vector)

fun World.getHighestLocationAtAsync(vector: Vector) =
    WorldUtils.getHighestLocationAtAsync(this, vector)

fun Location.getHighestBlockAtAsync() =
    WorldUtils.getHighestBlockAtAsync(this)

fun Location.toHighestLocationAsync() =
    WorldUtils.getHighestLocationAtAsync(this)
