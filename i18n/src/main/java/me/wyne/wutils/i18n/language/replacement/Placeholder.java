package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Placeholder {

    public static <T> TextReplacement replace(String key, T value)
    {
        return string -> string.replaceAll("<"+key+">", String.valueOf(value));
    }

    public static TextReplacement replace(String key, String value)
    {
        return string -> string.replaceAll("<"+key+">", value);
    }
    
    public static TextReplacement replace(String key, Component value)
    {
        return string -> string.replaceAll("<"+key+">", I18n.global.component().toString(value));
    }

    public static TextReplacement replace(String key, Component value, ComponentInterpreter interpreter)
    {
        return string -> string.replaceAll("<"+key+">", interpreter.toString(value));
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static TextReplacement plain(String key, Component value)
    {
        return string -> string.replaceAll("<"+key+">", PlainComponentSerializer.plain().serialize(value));
    }

    public static TextReplacement plainText(String key, Component value)
    {
        return string -> string.replaceAll("<"+key+">", PlainTextComponentSerializer.plainText().serialize(value));
    }

    public static TextReplacement legacy(String key, Component value)
    {
        return string -> string.replaceAll("<"+key+">", LegacyComponentSerializer.legacyAmpersand().serialize(value));
    }

    public static TextReplacement miniMessage(String key, Component value)
    {
        return string -> string.replaceAll("<"+key+">", MiniMessage.miniMessage().serialize(value));
    }

    public static TextReplacement regex(String regex, String value)
    {
        return string -> string.replaceAll(regex, value);
    }

}
