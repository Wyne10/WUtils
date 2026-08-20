package me.wyne.wutils.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits a delimited argument string into indexed tokens, e.g. a chat command's raw argument
 * line.
 *
 * <p>Delimiters are matched by regex (see {@link #COLON_DELIMITER}, {@link #SPACE_DELIMITER} and
 * {@link #COLON_OR_SPACE_DELIMITER}, the default). A double-quoted span (e.g. {@code "a b c"})
 * is kept as a single token even if it contains delimiter characters, so a value like a combined
 * WorldEdit mask ({@code "#surface #solid"}) survives splitting; the surrounding quotes are
 * stripped from the resulting token.
 */
public class Args {

    public static final @NotNull String COLON_DELIMITER = ":";
    public static final @NotNull String SPACE_DELIMITER = "\\s+";
    public static final @NotNull String COLON_OR_SPACE_DELIMITER = ":|\\s+";

    // A double-quoted span is kept as a single argument even if it contains delimiter characters,
    // so values like a combined WorldEdit mask ("#surface #solid") survive space splitting. Each
    // quoted span is swapped for an index placeholder wrapped in NUL (char 0), which no delimiter
    // matches, so it stays intact through split() and is restored to its content afterwards.
    private static final char MARKER = (char) 0;
    private static final Pattern QUOTED = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern PLACEHOLDER = Pattern.compile(MARKER + "(\\d+)" + MARKER);

    private final List<String> args;

    /**
     * Equivalent to {@link #Args(String, String) Args(string, COLON_OR_SPACE_DELIMITER)}.
     */
    public Args(@NotNull String string) {
        this(string, COLON_OR_SPACE_DELIMITER);
    }

    /**
     * @param regex the delimiter pattern to split on, outside of double-quoted spans
     */
    public Args(@NotNull String string, @NotNull String regex) {
        args = List.copyOf(split(string, regex));
    }

    /**
     * @return an immutable view of all parsed tokens, in order
     */
    public @NotNull List<@NotNull String> getArgs() {
        return args;
    }

    /**
     * @return the token at {@code index}, or {@code null} if there is no such argument
     */
    @Nullable
    public String getNullable(int index) {
        return args.size() <= index ? null : args.get(index);
    }

    /**
     * Equivalent to {@link #get(int, String) get(index, "")}.
     */
    @NotNull
    public String get(int index) {
        return get(index, "");
    }

    /**
     * @return the token at {@code index}, trimmed, or {@code def} if there is no such argument
     */
    @NotNull
    public String get(int index, @NotNull String def) {
        if (index >= args.size())
            return def;
        return args.get(index).trim();
    }

    public int size() {
        return args.size();
    }

    private static @NotNull List<String> split(@NotNull String string, @NotNull String regex) {
        String trimmed = string.trim();

        Matcher quoted = QUOTED.matcher(trimmed);
        if (!quoted.find())
            return List.of(trimmed.split(regex));

        List<String> contents = new ArrayList<>();
        StringBuilder masked = new StringBuilder();
        int last = 0;
        do {
            masked.append(trimmed, last, quoted.start());
            masked.append(MARKER).append(contents.size()).append(MARKER);
            contents.add(quoted.group(1));
            last = quoted.end();
        } while (quoted.find());
        masked.append(trimmed, last, trimmed.length());

        List<String> result = new ArrayList<>();
        for (String token : masked.toString().split(regex))
            result.add(restore(token, contents));
        return result;
    }

    private static @NotNull String restore(@NotNull String token, @NotNull List<String> contents) {
        if (token.indexOf(MARKER) < 0)
            return token;
        Matcher matcher = PLACEHOLDER.matcher(token);
        StringBuilder restored = new StringBuilder();
        while (matcher.find())
            matcher.appendReplacement(restored, Matcher.quoteReplacement(contents.get(Integer.parseInt(matcher.group(1)))));
        matcher.appendTail(restored);
        return restored.toString();
    }

}
