package me.wyne.wutils.i18n.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public record JsonLanguageStrings(JsonObject root) implements LanguageStrings {

    @Nullable
    private JsonElement resolve(String path) {
        JsonElement current = root;
        for (String key : path.split("\\.")) {
            if (current == null || !current.isJsonObject())
                return null;
            current = current.getAsJsonObject().get(key);
        }
        return current;
    }

    @Override
    public boolean contains(String path) {
        return resolve(path) != null;
    }

    @Override
    public boolean isList(String path) {
        JsonElement element = resolve(path);
        return element != null && element.isJsonArray();
    }

    @Override
    public List<String> getStringList(String path) {
        List<String> result = new LinkedList<>();
        JsonElement element = resolve(path);
        if (element == null || !element.isJsonArray())
            return result;
        for (JsonElement item : element.getAsJsonArray()) {
            if (item.isJsonPrimitive())
                result.add(item.getAsString());
        }
        return result;
    }

}
