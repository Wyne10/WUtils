package me.wyne.wutils.common.kotlin.inventory

import me.wyne.wutils.common.inventory.InventoryUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.addItem(vararg items: ItemStack) =
    InventoryUtils.addItem(this, *items)

fun Inventory.addItem(items: Collection<ItemStack>) =
    InventoryUtils.addItem(this, items)

fun Player.addItem(vararg items: ItemStack) =
    InventoryUtils.addItem(this, *items)

fun Player.addItem(items: Collection<ItemStack>) =
    InventoryUtils.addItem(this, items)

fun Player.addOrDrop(vararg items: ItemStack) =
    InventoryUtils.addOrDrop(this, *items)

fun Player.addOrDrop(items: Collection<ItemStack>) =
    InventoryUtils.addOrDrop(this, items)

fun Player.addOrDrop(setOwner: Boolean, vararg items: ItemStack) =
    InventoryUtils.addOrDrop(this, setOwner, *items)

fun Player.addOrDrop(setOwner: Boolean, items: Collection<ItemStack>) =
    InventoryUtils.addOrDrop(this, setOwner, items)

fun Player.drop(vararg items: ItemStack) =
    InventoryUtils.drop(this, *items)

fun Player.drop(items: Collection<ItemStack>) =
    InventoryUtils.drop(this, items)

fun Player.drop(setOwner: Boolean, vararg items: ItemStack) =
    InventoryUtils.drop(this, setOwner, *items)

fun Player.drop(setOwner: Boolean, items: Collection<ItemStack>) =
    InventoryUtils.drop(this, setOwner, items)

fun Location.drop(items: Collection<ItemStack>) =
    InventoryUtils.drop(this, items)

fun InventoryClickEvent.getAffectedItems() =
    InventoryUtils.getAffectedItems(this)