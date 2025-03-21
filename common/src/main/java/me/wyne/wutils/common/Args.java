package me.wyne.wutils.common;

import java.util.List;

public class Args {

    private final List<String> args;

    public Args(String string, String regex) {
        args = List.of(string.split(regex));
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
