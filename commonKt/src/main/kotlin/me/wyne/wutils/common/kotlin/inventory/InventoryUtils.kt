package me.wyne.wutils.common.kotlin.inventory

import me.wyne.wutils.common.inventory.InventoryUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.addItem(vararg items: ItemStack) =
    InventoryUtils.addItem(this, *items)

fun Player.addItem(vararg items: ItemStack) =
    InventoryUtils.addItem(this, *items)

fun Player.addOrDrop(vararg items: ItemStack) =
    InventoryUtils.addOrDrop(this, *items)