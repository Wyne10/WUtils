package me.wyne.wutils.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Args {

    public static final String COLON_DELIMITER = ":";
    public static final String SPACE_DELIMITER = "\\s+";
    public static final String COLON_OR_SPACE_DELIMITER = ":|\\s+";

    // A double-quoted span is kept as a single argument even if it contains delimiter characters,
    // so values like a combined WorldEdit mask ("#surface #solid") survive space splitting. Each
    // quoted span is swapped for an index placeholder wrapped in NUL (char 0), which no delimiter
    // matches, so it stays intact through split() and is restored to its content afterwards.
    private static final char MARKER = (char) 0;
    private static final Pattern QUOTED = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern PLACEHOLDER = Pattern.compile(MARKER + "(\\d+)" + MARKER);

    private final List<String> args;

    public Args(String string) {
        this(string, COLON_OR_SPACE_DELIMITER);
    }

    public Args(String string, String regex) {
        args = List.copyOf(split(string, regex));
    }

    public List<String> getArgs() {
        return args;
    }

    @Nullable
    public String getNullable(int index) {
        return args.size() <= index ? null : args.get(index);
    }

    @NotNull
    public String get(int index) {
        return get(index, "");
    }

    @NotNull
    public String get(int index, String def) {
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
