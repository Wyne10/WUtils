package me.wyne.wutils.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Args {

    public static final String COLON_DELIMITER = ":";
    public static final String SPACE_DELIMITER = "\\s+";
    public static final String COLON_OR_SPACE_DELIMITER = ":|\\s+";

    private final List<String> args;

    public Args(String string) {
        var split = string.trim().split(COLON_OR_SPACE_DELIMITER);
        args = List.of(split);
    }

    public Args(String string, String regex) {
        var split = string.trim().split(regex);
        args = List.of(split);
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

}
