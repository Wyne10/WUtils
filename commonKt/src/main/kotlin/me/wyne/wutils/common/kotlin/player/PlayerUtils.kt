package me.wyne.wutils.common.kotlin.player

import me.wyne.wutils.common.player.PlayerUtils
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

val Player.expToLevelUp: Long
    get() = PlayerUtils.expToLevelUp(level.toLong())

var Player.currentExp: Long
    get() = PlayerUtils.currentExp(this)
    set(value) = PlayerUtils.setExp(this, value)

fun Player.addExp(exp: Long) {
    PlayerUtils.addExp(this, exp)
}

val OfflinePlayer.exists: Boolean
    get() = PlayerUtils.exists(this)