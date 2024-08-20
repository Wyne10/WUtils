package me.wyne.wutils.i18n.language.replacement;

public class Placeholder {

    public static TextReplacement replace(String key, String value)
    {
        return (string) -> string.replaceAll("<"+key+">", value);
    }

    public static TextReplacement regex(String regex, String value)
    {
        return (string) -> string.replaceAll(regex, value);
    }

}
