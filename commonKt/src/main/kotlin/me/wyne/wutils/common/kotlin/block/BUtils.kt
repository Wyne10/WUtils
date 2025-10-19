package me.wyne.wutils.common.kotlin.block

import me.wyne.wutils.common.block.BUtils
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Block.breakActuallyNaturally(tool: ItemStack, player: Player) =
    BUtils.breakActuallyNaturally(this, tool, player)

val BlockFace.yaw: Float
    get() = BUtils.getYaw(this)
