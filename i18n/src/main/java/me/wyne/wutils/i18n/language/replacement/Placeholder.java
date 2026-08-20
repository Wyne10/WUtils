package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

/**
 * Builds {@link TextReplacement}s that substitute a {@code <key>} placeholder in a raw interpreter-formatted
 * string.
 *
 * <p><b>{@code key} is interpreted as a regular expression.</b> Each replacement is applied via
 * {@code String#replaceAll("<" + key + ">", ...)}; the replacement value is protected with
 * {@link Matcher#quoteReplacement}, but the key itself is not passed through {@link java.util.regex.Pattern#quote}.</p>
 */
public class Placeholder {

    public static <T> @NotNull TextReplacement replace(@NotNull String key, @NotNull T value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(String.valueOf(value)));
    }

    public static @NotNull TextReplacement replace(@NotNull String key, @NotNull String value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(value));
    }

    public static @NotNull TextReplacement replace(@NotNull String key, @NotNull Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(I18n.global.component().toString(value)));
    }

    public static @NotNull TextReplacement replace(@NotNull String key, @NotNull Component value, @NotNull ComponentInterpreter interpreter) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(interpreter.toString(value)));
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static @NotNull TextReplacement plain(@NotNull String key, @NotNull Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(PlainComponentSerializer.plain().serialize(value)));
    }

    public static @NotNull TextReplacement plainText(@NotNull String key, @NotNull Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(PlainTextComponentSerializer.plainText().serialize(value)));
    }

    public static @NotNull TextReplacement legacy(@NotNull String key, @NotNull Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(LegacyInterpreter.SERIALIZER.serialize(value)));
    }

    public static @NotNull TextReplacement miniMessage(@NotNull String key, @NotNull Component value) {
        return string -> string.replaceAll("<"+key+">", Matcher.quoteReplacement(MiniMessage.miniMessage().serialize(value)));
    }

    public static @NotNull TextReplacement regex(@NotNull String regex, @NotNull String value) {
        return string -> string.replaceAll(regex, Matcher.quoteReplacement(value));
    }

}
