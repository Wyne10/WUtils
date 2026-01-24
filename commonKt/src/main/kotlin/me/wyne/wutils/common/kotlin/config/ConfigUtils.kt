package me.wyne.wutils.common.kotlin.config

import me.wyne.wutils.common.config.ConfigUtils
import me.wyne.wutils.common.duration.TimeSpan
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffectType
import java.util.EnumSet
import kotlin.random.Random

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

fun ConfigurationSection.getTimeSpan(path: String) =
    ConfigUtils.getTimeSpan(this, path)

fun ConfigurationSection.getTimeSpan(path: String, def: TimeSpan) =
    ConfigUtils.getTimeSpan(this, path, def)

fun ConfigurationSection.getMillis(path: String) =
    ConfigUtils.getMillis(this, path)

fun ConfigurationSection.getMillis(path: String, def: Long) =
    ConfigUtils.getMillis(this, path, def)

fun ConfigurationSection.getTicks(path: String) =
    ConfigUtils.getTicks(this, path)

fun ConfigurationSection.getTicks(path: String, def: Long) =
    ConfigUtils.getTicks(this, path, def)

fun ConfigurationSection.getTimeSpanRange(path: String) =
    ConfigUtils.getTimeSpanRange(this, path)

fun ConfigurationSection.getVectorRange(path: String) =
    ConfigUtils.getVectorRange(this, path)

fun ConfigurationSection.getPotionTypeEnumSet(path: String): Set<PotionEffectType> =
    getStringList(path)
        .mapNotNull { runCatching { PotionEffectType.getByName(it) }.getOrNull() }
        .toSet()

fun ConfigurationSection.getMaterialEnumSet(path: String): Set<Material> =
    ConfigUtils.getMaterialEnumSet(this, path)

inline fun <reified E : Enum<E>> ConfigurationSection.getEnumSet(key: String): EnumSet<E> =
    ConfigUtils.getEnumSet(this, key, E::class.java)

inline fun <reified E : Enum<E>> ConfigurationSection.getKeyedEnumSet(key: String): EnumSet<E> =
    ConfigUtils.getKeyedEnumSet(this, key, E::class.java)

inline fun <reified E : Enum<E>> getByName(name: String?): E? =
    ConfigUtils.getByName(name, E::class.java)

inline fun <reified E : Enum<E>> getByKeyOrName(key: String?): E? =
    ConfigUtils.getByKeyOrName(key, E::class.java)

inline fun <reified E : Enum<E>> ConfigurationSection.getByKeyOrName(path: String?): E? =
    ConfigUtils.getByKeyOrName(this, path, E::class.java)

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

typealias DoubleRange = ClosedFloatingPointRange<Double>

fun ConfigurationSection.getDoubleRange(path: String, def: DoubleRange = 0.0..0.0): DoubleRange {
    val args = getString(path)?.split(RANGE_DELIMITER)
    args?.let {
        return it.first().toDouble()..it.last().toDouble()
    }
    return def
}

fun DoubleRange.random() =
    Random.nextDouble(start, endInclusive)