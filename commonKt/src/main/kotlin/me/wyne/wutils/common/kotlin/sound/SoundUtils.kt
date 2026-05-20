package me.wyne.wutils.common.kotlin.sound

import me.wyne.wutils.common.sound.SoundUtils
import org.bukkit.Sound

val Sound.adventure: net.kyori.adventure.sound.Sound
    get() = SoundUtils.getSound(this)

fun Sound.getSound(): net.kyori.adventure.sound.Sound =
    SoundUtils.getSound(this)

fun Sound.getSound(source: net.kyori.adventure.sound.Sound.Source): net.kyori.adventure.sound.Sound =
    SoundUtils.getSound(this, source)

fun Sound.getSound(volume: Float): net.kyori.adventure.sound.Sound =
    SoundUtils.getSound(this, volume)

fun Sound.getSound(volume: Float, pitch: Float): net.kyori.adventure.sound.Sound =
    SoundUtils.getSound(this, volume, pitch)

fun Sound.getSound(source: net.kyori.adventure.sound.Sound.Source, volume: Float, pitch: Float): net.kyori.adventure.sound.Sound =
    SoundUtils.getSound(this, source, volume, pitch)
