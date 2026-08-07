package me.wyne.wutils.common.kotlin.random

import me.wyne.wutils.common.random.RandomUtils

val <K, V : Number> Map<K, V>.weightedRandom: Map.Entry<K, V>?
    get() = RandomUtils.weightedRandom<K, V>(this)