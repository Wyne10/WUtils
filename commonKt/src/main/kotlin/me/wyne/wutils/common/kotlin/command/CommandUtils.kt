package me.wyne.wutils.common.kotlin.command

import dev.jorel.commandapi.IStringTooltip
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions

fun <T : Argument<*>> T.suggest(vararg strings: String): T =
    replaceSuggestions(ArgumentSuggestions.strings(*strings)) as T

fun <T : Argument<*>> T.suggest(strings: Collection<String>): T =
    replaceSuggestions(ArgumentSuggestions.strings(strings)) as T

fun <T : Argument<*>> T.tooltip(vararg tooltips: IStringTooltip): T =
    replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(*tooltips)) as T

fun <T : Argument<*>> T.tooltip(tooltips: Collection<IStringTooltip>): T =
    replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(tooltips)) as T