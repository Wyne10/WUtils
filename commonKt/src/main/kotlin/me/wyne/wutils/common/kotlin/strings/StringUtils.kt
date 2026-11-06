package me.wyne.wutils.common.kotlin.strings

import me.wyne.wutils.common.strings.StringUtils

fun Int.toRoman(): String =
    StringUtils.toRoman(this)
