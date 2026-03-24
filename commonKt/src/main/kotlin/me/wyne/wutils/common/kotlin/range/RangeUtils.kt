package me.wyne.wutils.common.kotlin.range

import me.wyne.wutils.common.range.ClosedIntRange
import me.wyne.wutils.common.range.DoubleRange
import kotlin.random.Random

typealias DoubleRange = ClosedFloatingPointRange<Double>

val ClosedIntRange.range: IntRange
    get() = this.min..this.max

val IntRange.range: ClosedIntRange
    get() = ClosedIntRange(this.first, this.last)

val DoubleRange.range: me.wyne.wutils.common.kotlin.range.DoubleRange
    get() = this.min..this.max

val me.wyne.wutils.common.kotlin.range.DoubleRange.range: DoubleRange
    get() = DoubleRange(this.start, this.endInclusive)

fun me.wyne.wutils.common.kotlin.range.DoubleRange.random() =
    Random.nextDouble(start, endInclusive)
