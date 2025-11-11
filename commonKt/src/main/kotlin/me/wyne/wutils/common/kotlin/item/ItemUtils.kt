package me.wyne.wutils.common.kotlin.item

import com.destroystokyo.paper.MaterialTags
import me.wyne.wutils.common.item.ItemUtils
import me.wyne.wutils.common.item.TileStateLoader
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockState
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

fun ItemStack?.isNullOrAir() =
    ItemUtils.isNullOrAir(this)

fun ItemStack?.isNotNullOrAir() =
    ItemUtils.isNotNullOrAir(this)

fun ItemStack.damageNaturally(player: Player) =
    ItemUtils.damageNaturally(this, player)

fun Collection<ItemStack>.dropActuallyNaturally(event: BlockBreakEvent) =
    ItemUtils.dropActuallyNaturally(this, event)

fun ItemStack.saveBlockState(blockState: BlockState) =
    ItemUtils.saveBlockState(this, blockState)

fun ItemStack.saveBlockState(blockState: BlockState, loader: TileStateLoader) =
    ItemUtils.saveBlockState(this, blockState, loader)

fun ItemStack.saveBlockStateExtended(blockState: BlockState) =
    ItemUtils.saveBlockStateExtended(this, blockState)

fun ItemStack.loadBlockState(blockState: BlockState) =
    ItemUtils.loadBlockState(this, blockState)

fun ItemStack.loadBlockState(blockState: BlockState, loader: TileStateLoader) =
    ItemUtils.loadBlockState(this, blockState, loader)

val ItemStack.fuelTicks: Short
    get() = this.type.fuelTicks

val Material.fuelTicks: Short
    get() = MATERIAL_BURN_TICKS.getOrDefault(this, 0).toShort()

val MATERIAL_BURN_TICKS = mapOf(
    Material.LAVA_BUCKET to 20000,
    Material.COAL_BLOCK to 16000,
    Material.DRIED_KELP_BLOCK to 4000,
    Material.BLAZE_ROD to 2400,
    Material.COAL to 1600,
    Material.CHARCOAL to 1600,
    Material.LADDER to 300,
    Material.CRAFTING_TABLE to 300,
    Material.CARTOGRAPHY_TABLE to 300,
    Material.FLETCHING_TABLE to 300,
    Material.SMITHING_TABLE to 300,
    Material.LOOM to 300,
    Material.BOOKSHELF to 300,
    Material.LECTERN to 300,
    Material.COMPOSTER to 300,
    Material.CHEST to 300,
    Material.TRAPPED_CHEST to 300,
    Material.BARREL to 300,
    Material.DAYLIGHT_DETECTOR to 300,
    Material.JUKEBOX to 300,
    Material.NOTE_BLOCK to 300,
    Material.CROSSBOW to 300,
    Material.BOW to 300,
    Material.FISHING_ROD to 300,
    Material.WOODEN_PICKAXE to 200,
    Material.WOODEN_SHOVEL to 200,
    Material.WOODEN_HOE to 200,
    Material.WOODEN_AXE to 200,
    Material.WOODEN_SWORD to 200,
    Material.BOWL to 100,
    Material.STICK to 100,
    Material.DEAD_BUSH to 100,
    Material.BAMBOO to 50,
    Material.SCAFFOLDING to 50
) + Tag.ITEMS_BOATS.values.associateWith { 1200 } +
        Tag.LOGS_THAT_BURN.values.associateWith { 300 } +
        Tag.PLANKS.values.associateWith { 300 } +
        Tag.WOODEN_SLABS.values.associateWith { 150 } +
        Tag.WOODEN_STAIRS.values.associateWith { 300 } +
        Tag.WOODEN_PRESSURE_PLATES.values.associateWith { 300 } +
        Tag.WOODEN_BUTTONS.values.associateWith { 100 } +
        Tag.WOODEN_TRAPDOORS.values.associateWith { 300 } +
        Tag.WOODEN_FENCES.values.associateWith { 300 } +
        Tag.WOODEN_DOORS.values.associateWith { 200 } +
        Tag.SIGNS.values.associateWith { 200 } +
        MaterialTags.WOODEN_GATES.values.associateWith { 300 } +
        Tag.ITEMS_BANNERS.values.associateWith { 300 } +
        Tag.SAPLINGS.values.associateWith { 100 } +
        Tag.WOOL.values.associateWith { 100 } +
        Tag.CARPETS.values.associateWith { 67 }
