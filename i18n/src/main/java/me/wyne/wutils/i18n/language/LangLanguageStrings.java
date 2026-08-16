package me.wyne.wutils.i18n.language;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record LangLanguageStrings(Map<String, String> values) implements LanguageStrings {

    @Override
    public boolean contains(String path) {
        return values.containsKey(path);
    }

    @Override
    public boolean isList(String path) {
        return false;
    }

    @Override
    public List<String> getStringList(String path) {
        List<String> result = new LinkedList<>();
        String value = values.get(path);
        if (value != null)
            result.add(value);
        return result;
    }

}
