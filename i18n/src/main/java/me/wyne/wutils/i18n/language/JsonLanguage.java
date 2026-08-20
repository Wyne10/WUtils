package me.wyne.wutils.i18n.language;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@link Language} backed by a JSON object file, parsed with Gson (available transitively via paper-api,
 * not a declared dependency of this module).
 *
 * <p>{@link #getStringMap()} is built from every string value in the object tree, at any depth, keyed by
 * its full dotted path — so {@code messages.welcome} resolves the same way through
 * {@link #getStringMap()} as it does through {@link #getStrings()}.</p>
 */
public class JsonLanguage implements Language {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final Logger logger;

    private final String languageCode;
    private final Locale locale;

    private final File languageFile;
    private final JsonObject root;
    private final LanguageStrings strings;
    private final Map<String, String> stringMap = new HashMap<>();

    /** Equivalent to {@link #JsonLanguage(Language, File, Logger)} with no default language. */
    public JsonLanguage(@NotNull File languageFile, @NotNull Logger logger) {
        this(null, languageFile, logger);
    }

    /**
     * Loads a JSON object from {@code languageFile}, then recursively back-fills any keys present in
     * {@code defaultLanguage}'s file but missing from this one, rewriting {@code languageFile} on disk if
     * anything changed. Skipped when {@code defaultLanguage} is {@code null} or its file is empty.
     */
    public JsonLanguage(@Nullable Language defaultLanguage, @NotNull File languageFile, @NotNull Logger logger) {
        this.logger = logger;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        this.root = loadJson(languageFile);
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.strings = new JsonLanguageStrings(root);
        flattenStrings(root, "");
    }

    /**
     * Records every string value reachable under {@code object} into {@link #getStringMap()}, keyed by its
     * full dotted path. Non-string primitives and arrays are skipped — arrays are reached through
     * {@link #getStrings()} instead.
     */
    private void flattenStrings(@NotNull JsonObject object, @NotNull String prefix) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String path = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            JsonElement value = entry.getValue();
            if (value.isJsonObject())
                flattenStrings(value.getAsJsonObject(), path);
            else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString())
                stringMap.put(path, value.getAsString());
        }
    }

    private void mergeDefaultStrings(@Nullable Language defaultLanguage, @NotNull File languageFile) {
        if (defaultLanguage == null)
            return;
        if (defaultLanguage.getLanguageFile().length() == 0)
            return;
        JsonObject defaults = loadJson(defaultLanguage.getLanguageFile());
        if (mergeMissing(defaults, root)) {
            writeJson(languageFile, root);
            logger.debug("Merged missing strings to {}", languageFile.getName());
        }
    }

    // Recursively copies entries present in `from` but absent from `to`, descending into nested objects
    // that exist on both sides; returns whether `to` was changed.
    private boolean mergeMissing(@NotNull JsonObject from, @NotNull JsonObject to) {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> entry : from.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!to.has(key)) {
                to.add(key, value);
                changed = true;
            } else if (value.isJsonObject() && to.get(key).isJsonObject()) {
                changed |= mergeMissing(value.getAsJsonObject(), to.getAsJsonObject(key));
            }
        }
        return changed;
    }

    /** Returns an empty object for a missing, empty or unreadable file rather than throwing. */
    private @NotNull JsonObject loadJson(@Nullable File file) {
        if (file == null || !file.exists() || file.length() == 0)
            return new JsonObject();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonElement parsed = new JsonParser().parse(reader);
            if (parsed.isJsonObject())
                return parsed.getAsJsonObject();
            logger.error("Language file {} does not contain a JSON object", file.getName());
        } catch (Exception e) {
            logger.error("An exception occurred trying to read language file {}", file.getName(), e);
        }
        return new JsonObject();
    }

    private void writeJson(@NotNull File file, @NotNull JsonObject object) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            logger.error("An exception occurred trying to write language file {}", file.getName(), e);
        }
    }

    @Override
    public @NotNull String getLanguageCode() {
        return languageCode;
    }

    @Override
    public @NotNull Locale getLocale() {
        return locale;
    }

    @Override
    public @NotNull File getLanguageFile() {
        return languageFile;
    }

    @Override
    public @NotNull LanguageStrings getStrings() {
        return strings;
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull String> getStringMap() {
        return stringMap;
    }

}
