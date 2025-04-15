package me.wyne.wutils.i18n.language.replacement;

import net.kyori.adventure.text.Component;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Pattern;

public class ComponentPlaceholder {

    public static ComponentReplacement replace(String key, String value)
    {
        return component -> component.replaceText(builder -> builder.matchLiteral("<"+key+">").replacement(value));
    }

    public static ComponentReplacement replace(String key, Component value)
    {
        return component -> component.replaceText(builder -> builder.matchLiteral("<"+key+">").replacement(value));
    }

    public static ComponentReplacement regex(@RegExp String regex, String value)
    {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static ComponentReplacement regex(@RegExp String regex, Component value)
    {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static ComponentReplacement regex(Pattern regex, String value)
    {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static ComponentReplacement regex(Pattern regex, Component value)
    {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }
    
}
