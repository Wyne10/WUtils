package me.wyne.wutils.common.kotlin.server

import me.wyne.wutils.common.plugin.PluginUtils
import org.bukkit.Server

val Server.currentVersion: Int
    get() = PluginUtils.getServerVersion()