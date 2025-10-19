package me.wyne.wutils.common.kotlin.item

import me.wyne.wutils.common.item.ItUtils
import me.wyne.wutils.common.item.TileStateLoader
import org.bukkit.block.BlockState
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

fun ItemStack.damageNaturally(player: Player) =
    ItUtils.damageNaturally(this, player)

fun Collection<ItemStack>.dropActuallyNaturally(event: BlockBreakEvent) =
    ItUtils.dropActuallyNaturally(this, event)

fun ItemStack.saveBlockState(blockState: BlockState) =
    ItUtils.saveBlockState(this, blockState)

fun ItemStack.saveBlockState(blockState: BlockState, loader: TileStateLoader) =
    ItUtils.saveBlockState(this, blockState, loader)

fun ItemStack.saveBlockStateExtended(blockState: BlockState) =
    ItUtils.saveBlockStateExtended(this, blockState)

fun ItemStack.loadBlockState(blockState: BlockState) =
    ItUtils.loadBlockState(this, blockState)

fun ItemStack.loadBlockState(blockState: BlockState, loader: TileStateLoader) =
    ItUtils.loadBlockState(this, blockState, loader)