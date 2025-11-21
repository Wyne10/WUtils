package me.wyne.wutils.common.kotlin.world

import me.wyne.wutils.common.world.WorldUtils
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

fun World.getHighestBlockAtAsync(x: Int, y: Int) =
    WorldUtils.getHighestBlockAtAsync(this, x, y)

fun World.getHighestLocationAtAsync(x: Int, y: Int) =
    WorldUtils.getHighestLocationAtAsync(this, x, y)

fun World.getHighestBlockAtAsync(x: Double, y: Double) =
    WorldUtils.getHighestBlockAtAsync(this, x, y)

fun World.getHighestLocationAtAsync(x: Double, y: Double) =
    WorldUtils.getHighestLocationAtAsync(this, x, y)

fun World.getHighestBlockAtAsync(vector: Vector) =
    WorldUtils.getHighestBlockAtAsync(this, vector)

fun World.getHighestLocationAtAsync(vector: Vector) =
    WorldUtils.getHighestLocationAtAsync(this, vector)

fun Location.getHighestBlockAtAsync() =
    WorldUtils.getHighestBlockAtAsync(this)

fun Location.toHighestLocationAsync() =
    WorldUtils.getHighestLocationAtAsync(this)
