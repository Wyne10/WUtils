package me.wyne.wutils.i18n.language;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FilenameUtils;
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

    public JsonLanguage(File languageFile, Logger logger) {
        this(null, languageFile, logger);
    }

    public JsonLanguage(@Nullable Language defaultLanguage, File languageFile, Logger logger) {
        this.logger = logger;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        this.root = loadJson(languageFile);
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.strings = new JsonLanguageStrings(root);
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString())
                stringMap.put(entry.getKey(), value.getAsString());
        }
    }

    private void mergeDefaultStrings(@Nullable Language defaultLanguage, File languageFile) {
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

    private boolean mergeMissing(JsonObject from, JsonObject to) {
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

    private JsonObject loadJson(File file) {
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

    private void writeJson(File file, JsonObject object) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            logger.error("An exception occurred trying to write language file {}", file.getName(), e);
        }
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public File getLanguageFile() {
        return languageFile;
    }

    @Override
    public LanguageStrings getStrings() {
        return strings;
    }

    @Override
    public Map<String, String> getStringMap() {
        return stringMap;
    }

}
