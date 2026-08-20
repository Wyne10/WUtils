package me.wyne.wutils.i18n.language.replacement;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Builds {@link ComponentReplacement}s that substitute a {@code <key>} placeholder within an Adventure
 * {@link Component} tree using {@link net.kyori.adventure.text.TextReplacementConfig.Builder#matchLiteral}.
 */
public class ComponentPlaceholder {

    public static @NotNull ComponentReplacement replace(@NotNull String key, @NotNull String value) {
        return component -> component.replaceText(builder -> builder.matchLiteral("<"+key+">").replacement(value));
    }

    public static @NotNull ComponentReplacement replace(@NotNull String key, @NotNull Component value) {
        return component -> component.replaceText(builder -> builder.matchLiteral("<"+key+">").replacement(value));
    }

    public static @NotNull ComponentReplacement replace(@NotNull String key, @NotNull BaseComponent[] value) {
        return component -> component.replaceText(builder -> builder.matchLiteral("<"+key+">").replacement(BungeeComponentSerializer.get().deserialize(value)));
    }

    public static @NotNull ComponentReplacement regex(@RegExp @NotNull String regex, @NotNull String value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static @NotNull ComponentReplacement regex(@RegExp @NotNull String regex, @NotNull Component value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static @NotNull ComponentReplacement regex(@RegExp @NotNull String regex, @NotNull BaseComponent[] value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(BungeeComponentSerializer.get().deserialize(value)));
    }

    public static @NotNull ComponentReplacement regex(@NotNull Pattern regex, @NotNull String value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static @NotNull ComponentReplacement regex(@NotNull Pattern regex, @NotNull Component value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(value));
    }

    public static @NotNull ComponentReplacement regex(@NotNull Pattern regex, @NotNull BaseComponent[] value) {
        return component -> component.replaceText(builder -> builder.match(regex).replacement(BungeeComponentSerializer.get().deserialize(value)));
    }

}
