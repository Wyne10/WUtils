package me.wyne.wutils.i18n.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/** {@link LanguageStrings} view over a Gson {@link JsonObject}, resolving paths split on {@code .}. */
public record JsonLanguageStrings(@NotNull JsonObject root) implements LanguageStrings {

    /** Returns the element at the dotted path, or {@code null} if any segment is missing or not an object. */
    @Nullable
    private JsonElement resolve(@NotNull String path) {
        JsonElement current = root;
        for (String key : path.split("\\.")) {
            if (current == null || !current.isJsonObject())
                return null;
            current = current.getAsJsonObject().get(key);
        }
        return current;
    }

    @Override
    public boolean contains(@NotNull String path) {
        return resolve(path) != null;
    }

    @Override
    public boolean isList(@NotNull String path) {
        JsonElement element = resolve(path);
        return element != null && element.isJsonArray();
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
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
