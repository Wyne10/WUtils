package me.wyne.wutils.i18n.kotlin

import me.wyne.wutils.i18n.I18n
import me.wyne.wutils.i18n.language.component.LocalizedComponent
import me.wyne.wutils.i18n.language.component.LocalizedString
import me.wyne.wutils.i18n.language.component.PlaceholderLocalizedComponent
import me.wyne.wutils.i18n.language.component.PlaceholderLocalizedString
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement
import me.wyne.wutils.i18n.language.replacement.TextReplacement
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun Player?.localizedString(path: String): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path)

fun Player?.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path, *replacements)

fun Player?.localizedStrings(path: String): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path)

fun Player?.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path, *replacements)

fun Player?.localizedComponent(path: String): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path)

fun Player?.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path, *replacements)

fun Player?.localizedComponents(path: String): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path)

fun Player?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path, *replacements)

fun Player?.placeholderString(path: String): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path)

fun Player?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path, *replacements)

fun Player?.placeholderStrings(path: String): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path)

fun Player?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path, *replacements)

fun Player?.placeholderComponent(path: String): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path)

fun Player?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path, *replacements)

fun Player?.placeholderComponents(path: String): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path)

fun Player?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path, *replacements)

fun OfflinePlayer?.localizedString(path: String): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path)

fun OfflinePlayer?.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path, *replacements)

fun OfflinePlayer?.localizedStrings(path: String): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path)

fun OfflinePlayer?.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path, *replacements)

fun OfflinePlayer?.localizedComponent(path: String): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path)

fun OfflinePlayer?.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path, *replacements)

fun OfflinePlayer?.localizedComponents(path: String): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path)

fun OfflinePlayer?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path, *replacements)

fun OfflinePlayer?.placeholderString(path: String): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path)

fun OfflinePlayer?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path, *replacements)

fun OfflinePlayer?.placeholderStrings(path: String): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path)

fun OfflinePlayer?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path, *replacements)

fun OfflinePlayer?.placeholderComponent(path: String): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path)

fun OfflinePlayer?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path, *replacements)

fun OfflinePlayer?.placeholderComponents(path: String): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path)

fun OfflinePlayer?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path, *replacements)

fun CommandSender.localizedString(path: String): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path)

fun CommandSender.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.getString(I18n.toLocale(this), path, *replacements)

fun CommandSender.localizedStrings(path: String): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path)

fun CommandSender.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.getStringList(I18n.toLocale(this), path, *replacements)

fun CommandSender.localizedComponent(path: String): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path)

fun CommandSender.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.getComponent(I18n.toLocale(this), path, *replacements)

fun CommandSender?.localizedComponents(path: String): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path)

fun CommandSender?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.getComponentList(I18n.toLocale(this), path, *replacements)

fun CommandSender?.placeholderString(path: String): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path)

fun CommandSender?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.getPlaceholderString(I18n.toLocale(this), this, path, *replacements)

fun CommandSender?.placeholderStrings(path: String): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path)

fun CommandSender?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.getPlaceholderStringList(I18n.toLocale(this), this, path, *replacements)

fun CommandSender?.placeholderComponent(path: String): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path)

fun CommandSender?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.getPlaceholderComponent(I18n.toLocale(this), this, path, *replacements)

fun CommandSender?.placeholderComponents(path: String): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path)

fun CommandSender?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.getPlaceholderComponentList(I18n.toLocale(this), this, path, *replacements)

fun Collection<String>.reduceRaw() =
    reduceOrNull(I18n::reduceRawString)

fun Collection<Component>.reduceRaw() =
    reduceOrNull(I18n::reduceRawComponent)

fun <T : LocalizedString> Collection<T>.reduce() =
    map { it.get() }.reduceOrNull(I18n::reduceRawString)

fun <T : LocalizedComponent> Collection<T>.reduce() =
    map { it.get() }.reduceOrNull(I18n::reduceRawComponent)

fun Collection<String>.of(op: (String) -> String) =
    map(op)

fun Collection<String>.ofComponents(op: (String) -> Component) =
    map(op)

fun <T : LocalizedString> Collection<String>.ofLocalized(op: (String) -> T) =
    map(op)

fun <T : LocalizedComponent> Collection<String>.ofLocalizedComponents(op: (String) -> T) =
    map(op)

fun <T : ComponentLike> Collection<T>.asComponents() =
    map(ComponentLike::asComponent)

fun <T : LocalizedComponent> Collection<LocalizedComponent>.apply(vararg replacements: ComponentReplacement) =
    map { it.replace(*replacements) }

fun Collection<String>.apply(vararg replacements: TextReplacement) =
    map { I18n.applyTextReplacements(it, *replacements) }

val CommandSender.player
    get() = this as? Player

val CommandSender.locale
    get() = this.player?.locale()

val Component.legacy
    get() = LegacyInterpreter.SERIALIZER.serialize(this)

val Component.gson
    get() = GsonComponentSerializer.gson().serialize(this)

@Suppress("DEPRECATION", "UnstableApiUsage")
val Component.plain
    get() = PlainComponentSerializer.plain().serialize(this)

val Component.plainText
    get() = PlainTextComponentSerializer.plainText().serialize(this)

val Component.miniMessage
    get() = MiniMessage.miniMessage().serialize(this)

val Component.bungee
    get() = BungeeComponentSerializer.get().serialize(this)

@Suppress("UnstableApiUsage")
val Component.minecraft
    get() = MinecraftComponentSerializer.get().serialize(this)

val String.legacy
    get() = LegacyInterpreter.SERIALIZER.deserialize(this)

val String.gson
    get() = GsonComponentSerializer.gson().deserialize(this)

@Suppress("DEPRECATION", "UnstableApiUsage")
val String.plain
    get() = PlainComponentSerializer.plain().deserialize(this)

val String.plainText
    get() = PlainTextComponentSerializer.plainText().deserialize(this)

val String.miniMessage
    get() = MiniMessage.miniMessage().deserialize(this)

val Array<BaseComponent>.component
    get() = BungeeComponentSerializer.get().deserialize(this)

@Suppress("UnstableApiUsage")
val Any.component
    get() = MinecraftComponentSerializer.get().deserialize(this)