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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "UnusedReturnValue", "ResultOfMethodCallIgnored"})
public class BaseI18nBuilder<T extends BaseI18nBuilder<?>> {

    private Logger log = LoggerFactory.getLogger(I18n.class);

    private ComponentAudiences componentAudiences = new NativeComponentAudiences();

    private final Map<String, Language> languageMap = new HashMap<>();
    private String defaultLanguageCode;

    private StringInterpreter stringInterpreter = new BaseInterpreter(new EmptyValidator());
    private ComponentInterpreter componentInterpreter = new LegacyInterpreter(new EmptyValidator());

    private boolean usePlayerLanguage = true;

    static {
        try {
            Configurator.setLevel("ru.vyarus", Level.WARN);
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
    }

    public T setLog(Logger log) {
        this.log = log;
        return (T) this;
    }

    public T setComponentAudience(ComponentAudiences componentAudiences) {
        this.componentAudiences = componentAudiences;
        return (T) this;
    }

    public T setStringInterpreter(StringInterpreter stringInterpreter) {
        this.stringInterpreter = stringInterpreter;
        return (T) this;
    }

    public T setComponentInterpreter(ComponentInterpreter componentInterpreter) {
        this.componentInterpreter = componentInterpreter;
        return (T) this;
    }

    public T setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
        return (T) this;
    }

    public T setUsePlayerLanguage(boolean usePlayerLanguage) {
        this.usePlayerLanguage = usePlayerLanguage;
        return (T) this;
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

    @SuppressWarnings("DataFlowIssue")
    public T loadLanguages(File directory) {
        if (!directory.exists())
            directory.mkdirs();

        for (File file : directory.listFiles()) {
            if (!file.isFile()) continue;
            loadLanguage(file);
        }
        return (T) this;
    }

    public T loadLanguage(File languageFile) {
        loadLanguage(null, languageFile);
        return (T) this;
    }

    public T loadLanguage(@Nullable Language defaultLanguage, File languageFile) {
        String languageCode = getLanguageCode(languageFile);
        if (languageMap.containsKey(languageCode))
            return (T) this;
        languageMap.put(languageCode, new BaseLanguage(defaultLanguage, languageFile, log));
        log.debug("Loaded {} language", languageCode);
        return (T) this;
    }

    public T loadLanguage(Plugin plugin, String languageResourcePath) {
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        if (!languageFile.exists())
            plugin.saveResource(languageResourcePath, false);
        File languageResourceFile = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResourceFile);
        } catch (IOException e) {
            getLog().error("An exception occurred trying to write resource {} to a file", languageResourcePath, e);
        }
        loadLanguage(new BaseLanguage(languageResourceFile, getLog()), languageFile);
        return (T) this;
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
        return new I18n(componentAudiences, languageMap, getDefaultLanguage(), stringInterpreter, componentInterpreter, usePlayerLanguage);
    }

}
