package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Placeholder {

    public static TextReplacement replace(String key, String value)
    {
        return (string) -> string.replaceAll("<"+key+">", value);
    }

    public static TextReplacement legacy(String key, Component value)
    {
        return (string) -> string.replaceAll("<"+key+">", LegacyComponentSerializer.legacyAmpersand().serialize(value));
    }

    public static TextReplacement regex(String regex, String value)
    {
        return (string) -> string.replaceAll(regex, value);
    }

}
