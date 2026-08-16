package me.wyne.wutils.i18n.language;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class LangLanguage implements Language {

    private final Logger logger;

    private final String languageCode;
    private final Locale locale;

    private final File languageFile;
    private final LanguageStrings strings;
    private final Map<String, String> stringMap;

    public LangLanguage(File languageFile, Logger logger) {
        this(null, languageFile, logger);
    }

    public LangLanguage(@Nullable Language defaultLanguage, File languageFile, Logger logger) {
        this.logger = logger;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        this.stringMap = loadLang(languageFile);
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.strings = new LangLanguageStrings(stringMap);
    }

    private void mergeDefaultStrings(@Nullable Language defaultLanguage, File languageFile) {
        if (defaultLanguage == null)
            return;
        if (defaultLanguage.getLanguageFile().length() == 0)
            return;
        Map<String, String> missing = new LinkedHashMap<>();
        loadLang(defaultLanguage.getLanguageFile()).forEach((key, value) -> {
            if (!stringMap.containsKey(key))
                missing.put(key, value);
        });
        if (missing.isEmpty())
            return;
        stringMap.putAll(missing);
        appendLang(languageFile, missing);
        logger.debug("Merged missing strings to {}", languageFile.getName());
    }

    private Map<String, String> loadLang(File file) {
        Map<String, String> result = new LinkedHashMap<>();
        if (file == null || !file.exists() || file.length() == 0)
            return result;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#"))
                    continue;
                int separator = line.indexOf('=');
                if (separator < 0)
                    continue;
                String key = line.substring(0, separator).trim();
                if (!key.isEmpty())
                    result.put(key, line.substring(separator + 1));
            }
        } catch (IOException e) {
            logger.error("An exception occurred trying to read language file {}", file.getName(), e);
        }
        return result;
    }

    private void appendLang(File file, Map<String, String> entries) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            if (file.length() > 0)
                writer.write(System.lineSeparator());
            for (Map.Entry<String, String> entry : entries.entrySet())
                writer.write(entry.getKey() + "=" + entry.getValue() + System.lineSeparator());
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
