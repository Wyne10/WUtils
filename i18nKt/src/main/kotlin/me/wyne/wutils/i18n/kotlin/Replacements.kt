package me.wyne.wutils.i18n.kotlin

import me.wyne.wutils.i18n.language.replacement.ComponentPlaceholder
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement
import me.wyne.wutils.i18n.language.replacement.Placeholder
import me.wyne.wutils.i18n.language.replacement.TextReplacement
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import java.util.regex.Pattern

infix fun <T> String.replace(value: T): TextReplacement =
    Placeholder.replace(this, value)

infix fun String.replace(value: String): TextReplacement =
    Placeholder.replace(this, value)

infix fun String.replace(value: Component): TextReplacement =
    Placeholder.replace(this, value)

infix fun String.plain(value: Component): TextReplacement =
    Placeholder.plain(this, value)

infix fun String.plainText(value: Component): TextReplacement =
    Placeholder.plainText(this, value)

infix fun String.legacy(value: Component): TextReplacement =
    Placeholder.legacy(this, value)

infix fun String.miniMessage(value: Component): TextReplacement =
    Placeholder.miniMessage(this, value)

infix fun String.regex(value: String): TextReplacement =
    Placeholder.regex(this, value)

infix fun String.replaceComponent(value: String): ComponentReplacement =
    ComponentPlaceholder.replace(this, value)

infix fun String.replaceComponent(value: Component): ComponentReplacement =
    ComponentPlaceholder.replace(this, value)

infix fun String.replaceComponent(value: Array<BaseComponent>): ComponentReplacement =
    ComponentPlaceholder.replace(this, value)

infix fun String.regexComponent(value: String): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun String.regexComponent(value: Component): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun String.regexComponent(value: Array<BaseComponent>): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun Pattern.regexComponent(value: String): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun Pattern.regexComponent(value: Component): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun Pattern.regexComponent(value: Array<BaseComponent>): ComponentReplacement =
    ComponentPlaceholder.regex(this, value)

infix fun TextReplacement.andThen(replacement: TextReplacement): TextReplacement =
    this.then(replacement) as TextReplacement

infix fun ComponentReplacement.andThen(replacement: ComponentReplacement): ComponentReplacement =
    this.then(replacement) as ComponentReplacement



