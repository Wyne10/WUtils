package me.wyne.wutils.common.kotlin.block

import me.wyne.wutils.common.block.BlockUtils
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Block.breakActuallyNaturally(tool: ItemStack, player: Player) =
    BlockUtils.breakActuallyNaturally(this, tool, player)

val BlockFace.yaw: Float
    get() = BlockUtils.getYaw(this)
