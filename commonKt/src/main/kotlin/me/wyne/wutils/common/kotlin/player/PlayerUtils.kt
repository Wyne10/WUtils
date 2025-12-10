package me.wyne.wutils.common.kotlin.player

import me.wyne.wutils.common.player.PlayerUtils
import org.bukkit.entity.Player

val Player.expToLevelUp: Int
    get() = PlayerUtils.expToLevelUp(level)

val Player.currentExp: Int
    get() = PlayerUtils.currentExp(this)

fun Player.setCurrentExp(exp: Int) {
    PlayerUtils.setExp(this, exp)
}

fun Player.addExp(exp: Int) {
    PlayerUtils.addExp(this, exp)
}