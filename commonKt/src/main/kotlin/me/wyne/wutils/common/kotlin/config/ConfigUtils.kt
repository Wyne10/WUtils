package me.wyne.wutils.common.kotlin.config

import me.wyne.wutils.common.config.ConfigUtils
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffectType

const val RANGE_DELIMITER = ".."
const val COMMA_DELIMITER = ","
const val COLON_DELIMITER = ":"
const val SPACE_DELIMITER = " "

fun ConfigurationSection.getVectorOrZero(path: String) =
    ConfigUtils.getVectorOrZero(this, path)

fun ConfigurationSection.getIntComparator(path: String) =
    ConfigUtils.getIntComparator(this, path)

fun ConfigurationSection.getDoubleComparator(path: String) =
    ConfigUtils.getDoubleComparator(this, path)

fun ConfigurationSection.getIntOperation(path: String) =
    ConfigUtils.getIntOperation(this, path)

fun ConfigurationSection.getDoubleOperation(path: String) =
    ConfigUtils.getDoubleOperation(this, path)

fun ConfigurationSection.getMillis(path: String) =
    ConfigUtils.getMillis(this, path)

fun ConfigurationSection.getTicks(path: String) =
    ConfigUtils.getTicks(this, path)

fun ConfigurationSection.getPotionTypeEnumSet(path: String): Set<PotionEffectType> =
    getStringList(path)
        .mapNotNull { runCatching { PotionEffectType.getByName(it) }.getOrNull() }
        .toSet()

inline fun <reified E : Enum<E>> ConfigurationSection.getEnumSet(key: String): Set<E> {
    return if (isBoolean(key) && getBoolean(key)) {
        enumValues<E>().toSet()
    } else {
        getStringList(key)
            .mapNotNull { runCatching { enumValueOf<E>(it) }.getOrNull() }
            .toSet()
    }
}

fun ConfigurationSection.getMaterial(path: String, def: Material = Material.STONE): Material =
    getString(path)?.let { Material.matchMaterial(it) } ?: def

fun ConfigurationSection.getBlockMaterial(path: String, def: Material = Material.STONE): Material =
    getMaterial(path, def)
        .also { if (!it.isBlock) throw IllegalArgumentException("Expected block at path ${"$currentPath.$path"}") }

fun ConfigurationSection.getIntRange(path: String, def: IntRange = 0..0): IntRange {
    val args = getString(path)?.split(RANGE_DELIMITER)
    args?.let {
        return IntRange(it.first().toInt(), it.last().toInt())
    }
    return def
}