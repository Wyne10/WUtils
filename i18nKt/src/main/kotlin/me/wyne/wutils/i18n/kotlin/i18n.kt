package me.wyne.wutils.i18n.kotlin

import me.wyne.wutils.i18n.I18n
import me.wyne.wutils.i18n.language.component.LocalizedComponent
import me.wyne.wutils.i18n.language.component.LocalizedString
import me.wyne.wutils.i18n.language.component.PlaceholderLocalizedComponent
import me.wyne.wutils.i18n.language.component.PlaceholderLocalizedString
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement
import me.wyne.wutils.i18n.language.replacement.TextReplacement
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun Player?.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.accessor(this, path).getString(*replacements)

fun Player?.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.accessor(this, path).getStringList(*replacements)

fun Player?.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.accessor(this, path).getComponent(*replacements)

fun Player?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.accessor(this, path).getComponentList(*replacements)

fun Player?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.accessor(this, path).getPlaceholderString(this, *replacements)

fun Player?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.accessor(this, path).getPlaceholderStringList(this, *replacements)

fun Player?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.accessor(this, path).getPlaceholderComponent(this, *replacements)

fun Player?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.accessor(this, path).getPlaceholderComponentList(this, *replacements)

fun OfflinePlayer?.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.accessor(this, path).getString(*replacements)

fun OfflinePlayer?.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.accessor(this, path).getStringList(*replacements)

fun OfflinePlayer?.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.accessor(this, path).getComponent(*replacements)

fun OfflinePlayer?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.accessor(this, path).getComponentList(*replacements)

fun OfflinePlayer?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.accessor(this, path).getPlaceholderString(this, *replacements)

fun OfflinePlayer?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.accessor(this, path).getPlaceholderStringList(this, *replacements)

fun OfflinePlayer?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.accessor(this, path).getPlaceholderComponent(this, *replacements)

fun OfflinePlayer?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.accessor(this, path).getPlaceholderComponentList(this, *replacements)

fun CommandSender.localizedString(path: String, vararg replacements: TextReplacement): LocalizedString =
    I18n.global.accessor(this, path).getString(*replacements)

fun CommandSender.localizedStrings(path: String, vararg replacements: TextReplacement): List<LocalizedString> =
    I18n.global.accessor(this, path).getStringList(*replacements)

fun CommandSender.localizedComponent(path: String, vararg replacements: TextReplacement): LocalizedComponent =
    I18n.global.accessor(this, path).getComponent(*replacements)

fun CommandSender?.localizedComponents(path: String, vararg replacements: TextReplacement): List<LocalizedComponent> =
    I18n.global.accessor(this, path).getComponentList(*replacements)

fun CommandSender?.placeholderString(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedString =
    I18n.global.accessor(this, path).getPlaceholderString(this, *replacements)

fun CommandSender?.placeholderStrings(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedString> =
    I18n.global.accessor(this, path).getPlaceholderStringList(this, *replacements)

fun CommandSender?.placeholderComponent(path: String, vararg replacements: TextReplacement): PlaceholderLocalizedComponent =
    I18n.global.accessor(this, path).getPlaceholderComponent(this, *replacements)

fun CommandSender?.placeholderComponents(path: String, vararg replacements: TextReplacement): List<PlaceholderLocalizedComponent> =
    I18n.global.accessor(this, path).getPlaceholderComponentList(this, *replacements)

fun Collection<String>.reduceRaw() =
    reduceOrNull(I18n::reduceRawString)

fun Collection<Component>.reduceRaw() =
    reduceOrNull(I18n::reduceRawComponent)

fun <T : LocalizedString> Collection<T>.reduce() =
    map { it.get() }.reduceOrNull(I18n::reduceRawString)

fun <T : LocalizedComponent> Collection<T>.reduce() =
    map { it.get() }.reduceOrNull(I18n::reduceRawComponent)

fun <T : ComponentLike> Collection<T>.asComponents() =
    map(ComponentLike::asComponent)

fun <T : LocalizedComponent> Collection<T>.apply(vararg replacements: ComponentReplacement) =
    map { it.replace(*replacements) }

fun Collection<String>.apply(vararg replacements: TextReplacement) =
    map { I18n.applyTextReplacements(it, *replacements) }

val CommandSender.player
    get() = this as? Player

val CommandSender.locale
    get() = this.player?.locale()

val Component.legacy
    get() = I18n.serializeLegacy(this)

val Component.legacySection
    get() = I18n.serializeLegacySection(this)

val Component.gson
    get() = I18n.serializeGson(this)

val Component.plain
    get() = I18n.serializePlain(this)

val Component.plainText
    get() = I18n.serializePlainText(this)

val Component.miniMessage
    get() = I18n.serializeMiniMessage(this)

val Component.bungee
    get() = I18n.serializeBungee(this)

val String.legacy
    get() = I18n.deserializeLegacy(this)

val String.legacySection
    get() = I18n.deserializeLegacySection(this)

val String.gson
    get() = I18n.deserializeGson(this)

val String.plain
    get() = I18n.deserializePlain(this)

val String.plainText
    get() = I18n.deserializePlainText(this)

val String.miniMessage
    get() = I18n.deserializeMiniMessage(this)

val Array<BaseComponent>.component
    get() = I18n.deserializeBungee(this)