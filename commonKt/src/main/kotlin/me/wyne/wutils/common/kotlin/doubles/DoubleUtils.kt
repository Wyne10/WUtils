package me.wyne.wutils.common.kotlin.doubles

import kotlin.random.Random

fun Double.test(): Boolean =
    Random.nextDouble() < this