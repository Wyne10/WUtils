package me.wyne.wutils.common.kotlin.range

import me.wyne.wutils.common.range.ClosedIntRange
import me.wyne.wutils.common.range.DoubleRange
import me.wyne.wutils.common.range.LocationRange
import me.wyne.wutils.common.range.Range
import org.bukkit.Location

typealias DoubleRange = ClosedFloatingPointRange<Double>

val ClosedIntRange.range: IntRange
    get() = this.min..this.max

val IntRange.range: ClosedIntRange
    get() = ClosedIntRange(this.first, this.last)

val DoubleRange.range: me.wyne.wutils.common.kotlin.range.DoubleRange
    get() = this.min..this.max

val me.wyne.wutils.common.kotlin.range.DoubleRange.range: DoubleRange
    get() = DoubleRange(this.start, this.endInclusive)

/**
 * Returns a uniform sample from this closed range, inclusive of [ClosedFloatingPointRange.endInclusive].
 *
 * Delegates to [Range.randomInclusive], so a single-point range yields its value instead of
 * throwing, and the upper bound is genuinely reachable rather than excluded.
 *
 * @throws IllegalArgumentException if the range is empty, i.e. its start exceeds its end
 */
fun me.wyne.wutils.common.kotlin.range.DoubleRange.random(): Double {
    require(!isEmpty()) { "Cannot sample an empty range: $this" }
    return Range.randomInclusive(start, endInclusive)
}

/** As [random], but returns `null` for an empty range instead of throwing. */
fun me.wyne.wutils.common.kotlin.range.DoubleRange.randomOrNull(): Double? =
    if (isEmpty()) null else Range.randomInclusive(start, endInclusive)

fun LocationRange.locations(step: Double = 1.0): Iterable<Location> =
    Iterable { locationIterator(step) }