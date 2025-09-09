package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.BaseLanguage;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.component.ComponentAudiences;
import me.wyne.wutils.i18n.language.component.NativeComponentAudiences;
import me.wyne.wutils.i18n.language.interpretation.BaseInterpreter;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BaseI18nBuilder {

    private Logger log = LoggerFactory.getLogger(I18n.class);

    private ComponentAudiences componentAudiences = new NativeComponentAudiences();

    private final Map<String, Language> languageMap = new HashMap<>();
    private String defaultLanguageCode;

    private StringInterpreter stringInterpreter = new BaseInterpreter(new EmptyValidator());
    private ComponentInterpreter componentInterpreter = new LegacyInterpreter(new EmptyValidator());

    private boolean usePlayerLanguage = true;

    public void setLog(Logger log) {
        this.log = log;
    }

    public void setComponentAudience(ComponentAudiences componentAudiences) {
        this.componentAudiences = componentAudiences;
    }

    public void setStringInterpreter(StringInterpreter stringInterpreter) {
        this.stringInterpreter = stringInterpreter;
    }

    public void setComponentInterpreter(ComponentInterpreter componentInterpreter) {
        this.componentInterpreter = componentInterpreter;
    }

    public void setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
    }

    public void setUsePlayerLanguage(boolean usePlayerLanguage) {
        this.usePlayerLanguage = usePlayerLanguage;
    }

    public Logger getLog() {
        return log;
    }

    public ComponentAudiences getComponentAudience() {
        return componentAudiences;
    }

    public Map<String, Language> getLanguageMap() {
        return languageMap;
    }

    public String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public StringInterpreter getStringInterpreter() {
        return stringInterpreter;
    }

    public ComponentInterpreter getComponentInterpreter() {
        return componentInterpreter;
    }

    public boolean isUsePlayerLanguage() {
        return usePlayerLanguage;
    }

    public void loadLanguages(File directory) {
        if (!directory.exists())
            directory.mkdirs();

        for (File file : directory.listFiles()) {
            if (!file.isFile()) continue;
            loadLanguage(file);
        }
    }

    public void loadLanguage(File languageFile) {
        loadLanguage(null, languageFile);
    }

    public void loadLanguage(@Nullable Language defaultLanguage, File languageFile) {
        String languageCode = getLanguageCode(languageFile);
        if (languageMap.containsKey(languageCode))
            return;
        languageMap.put(languageCode, new BaseLanguage(defaultLanguage, languageFile, log));
        log.info("Loaded {} language", languageCode);
    }

    public String getLanguageCode(File languageFile) {
        return FilenameUtils.removeExtension(languageFile.getName());
    }

    public Language getDefaultLanguage() {
        if (!languageMap.containsKey(defaultLanguageCode))
            throw new NullPointerException("Default language is null or not loaded");
        return languageMap.get(defaultLanguageCode);
    }

    public I18n build() {
        return new I18n(componentAudiences, getDefaultLanguage(), stringInterpreter, componentInterpreter, usePlayerLanguage);
    }

}
