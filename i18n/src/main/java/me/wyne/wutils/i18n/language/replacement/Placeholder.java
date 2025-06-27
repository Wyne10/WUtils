package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;

public class Placeholder {

    public static <T> TextReplacement replace(String key, T value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(String.valueOf(value)));
    }

    public static TextReplacement replace(String key, String value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(value));
    }

    public static TextReplacement replace(String key, Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(I18n.global.component().toString(value)));
    }

    public static TextReplacement replace(String key, Component value, ComponentInterpreter interpreter) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(interpreter.toString(value)));
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static TextReplacement plain(String key, Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(PlainComponentSerializer.plain().serialize(value)));
    }

    public static TextReplacement plainText(String key, Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(PlainTextComponentSerializer.plainText().serialize(value)));
    }

    public static TextReplacement legacy(String key, Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(LegacyInterpreter.SERIALIZER.serialize(value)));
    }

    public static TextReplacement miniMessage(String key, Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(MiniMessage.miniMessage().serialize(value)));
    }

    public static TextReplacement regex(String regex, String value) {
        return string -> string.replaceAll(regex, Matcher.quoteReplacement(value));
    }

}
