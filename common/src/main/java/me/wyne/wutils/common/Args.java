package me.wyne.wutils.common;

import java.util.Collections;
import java.util.List;

public class Args {

    private final List<String> args;

    public Args(String string, String regex) {
        var split = string.split(regex);
        if (split.length == 1)
            args = Collections.emptyList();
        else
            args = List.of(split);
    }

    public String get(int index) {
        if (index >= args.size())
            return "";
        return args.get(index).trim();
    }

    public String get(int index, String def) {
        if (index >= args.size())
            return def;
        return args.get(index).trim();
    }

    public int size() {
        return args.size();
    }

}
