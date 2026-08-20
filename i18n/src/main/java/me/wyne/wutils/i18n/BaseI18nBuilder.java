package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.JsonLanguage;
import me.wyne.wutils.i18n.language.LangLanguage;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.YamlLanguage;
import me.wyne.wutils.i18n.language.component.ComponentAudiences;
import me.wyne.wutils.i18n.language.component.PaperComponentAudiences;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic self-typed builder for {@link I18n}: configures the interpreters, the {@link ComponentAudiences}
 * and the loaded {@link Language}s, then produces an {@link I18n}. {@code T} is the concrete builder type,
 * allowing setters declared here to return the subclass type without a cast.
 *
 * <p>{@link PluginI18nBuilder} extends this with plugin-aware language and default-language resolution.</p>
 */
@SuppressWarnings({"unchecked", "UnusedReturnValue", "ResultOfMethodCallIgnored"})
public class BaseI18nBuilder<T extends BaseI18nBuilder<?>> {

    private Logger logger = PluginUtils.getLogger(getClass());

    private ComponentAudiences componentAudiences = new PaperComponentAudiences();

    private final Map<String, Language> languageMap = new HashMap<>();
    private String defaultLanguageCode;

    private StringInterpreter stringInterpreter = new BaseInterpreter(new EmptyValidator());
    private ComponentInterpreter componentInterpreter = new LegacyInterpreter(new EmptyValidator());

    private boolean usePlayerLanguage = true;

    static {
        // Quiets yaml-config-updater's "ru.vyarus" logger, whose default INFO output is noisy for consumers;
        // NoSuchMethodError is swallowed to tolerate log4j-core versions without this Configurator overload.
        try {
            Configurator.setLevel("ru.vyarus", Level.WARN);
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
    }

    public @NotNull T setLogger(@NotNull Logger logger) {
        this.logger = logger;
        return (T) this;
    }

    public @NotNull T setComponentAudience(@NotNull ComponentAudiences componentAudiences) {
        this.componentAudiences = componentAudiences;
        return (T) this;
    }

    public @NotNull T setStringInterpreter(@NotNull StringInterpreter stringInterpreter) {
        this.stringInterpreter = stringInterpreter;
        return (T) this;
    }

    public @NotNull T setComponentInterpreter(@NotNull ComponentInterpreter componentInterpreter) {
        this.componentInterpreter = componentInterpreter;
        return (T) this;
    }

    public @NotNull T setDefaultLanguageCode(@NotNull String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
        return (T) this;
    }

    public @NotNull T setUsePlayerLanguage(boolean usePlayerLanguage) {
        this.usePlayerLanguage = usePlayerLanguage;
        return (T) this;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public @NotNull ComponentAudiences getComponentAudience() {
        return componentAudiences;
    }

    public @NotNull Map<@NotNull String, @NotNull Language> getLanguageMap() {
        return languageMap;
    }

    /**
     * Returns the configured default language code, or {@code null} if none has been set yet — e.g.
     * before {@link #setDefaultLanguageCode} or {@link PluginI18nBuilder}'s config-driven resolution runs.
     */
    public @Nullable String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public @NotNull StringInterpreter getStringInterpreter() {
        return stringInterpreter;
    }

    public @NotNull ComponentInterpreter getComponentInterpreter() {
        return componentInterpreter;
    }

    public boolean isUsePlayerLanguage() {
        return usePlayerLanguage;
    }

    /** Loads every file in {@code directory} via {@link #loadLanguage(File)}, creating it first if missing. */
    @SuppressWarnings("DataFlowIssue")
    public @NotNull T loadLanguages(@NotNull File directory) {
        if (!directory.exists())
            directory.mkdirs();

        for (File file : directory.listFiles()) {
            if (!file.isFile()) continue;
            loadLanguage(file);
        }
        return (T) this;
    }

    /** Loads {@code languageFile} with no default language to back-fill missing keys from. */
    public @NotNull T loadLanguage(@NotNull File languageFile) {
        loadLanguage(null, languageFile);
        return (T) this;
    }

    /**
     * Loads {@code languageFile}, dispatching on its extension via {@link #createLanguage}, and registers
     * it under its {@link #getLanguageCode code}. First write wins: does nothing if a language with the
     * same code is already loaded.
     */
    public @NotNull T loadLanguage(@Nullable Language defaultLanguage, @NotNull File languageFile) {
        String languageCode = getLanguageCode(languageFile);
        if (getLanguageMap().containsKey(languageCode))
            return (T) this;
        getLanguageMap().put(languageCode, createLanguage(defaultLanguage, languageFile));
        getLogger().debug("Loaded {} language", languageCode);
        return (T) this;
    }

    /**
     * Creates a {@link Language} for {@code languageFile}, dispatching on its extension: {@code .json} to
     * {@link JsonLanguage}, {@code .lang} to {@link LangLanguage}, and anything else — not only
     * {@code .yml} — to {@link YamlLanguage}.
     */
    public @NotNull Language createLanguage(@Nullable Language defaultLanguage, @NotNull File languageFile) {
        String extension = FilenameUtils.getExtension(languageFile.getName());
        if (extension.equalsIgnoreCase("json"))
            return new JsonLanguage(defaultLanguage, languageFile, getLogger());
        if (extension.equalsIgnoreCase("lang"))
            return new LangLanguage(defaultLanguage, languageFile, getLogger());
        return new YamlLanguage(defaultLanguage, languageFile, getLogger());
    }

    /**
     * Copies the plugin resource at {@code languageResourcePath} into the plugin's data folder if it is
     * not already there, mirrors the bundled resource under {@code defaults/}, and loads the on-disk file
     * as a language using the mirrored bundled copy as its default (to back-fill missing keys from).
     *
     * @throws NullPointerException if the plugin has no such resource
     */
    public @NotNull T loadLanguage(@NotNull Plugin plugin, @NotNull String languageResourcePath) {
        if (plugin.getResource(languageResourcePath) == null)
            throw new NullPointerException("Language resource " + languageResourcePath + " not found");
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        if (!languageFile.exists())
            plugin.saveResource(languageResourcePath, false);
        File languageResourceFile = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResourceFile);
        } catch (IOException e) {
            getLogger().error("An exception occurred trying to write resource {} to a file", languageResourcePath, e);
        }
        loadLanguage(createLanguage(null, languageResourceFile), languageFile);
        return (T) this;
    }

    /**
     * Returns the language code for {@code languageFile}: its filename without extension. Files sharing a
     * base name but differing in extension (e.g. {@code en.yml} and {@code en.json}) collide on this code.
     */
    public @NotNull String getLanguageCode(@NotNull File languageFile) {
        return FilenameUtils.removeExtension(languageFile.getName());
    }

    /**
     * Returns the configured default {@link Language}.
     *
     * @throws NullPointerException if no default language code is set, or it does not match a loaded language
     */
    public @NotNull Language getDefaultLanguage() {
        if (!getLanguageMap().containsKey(getDefaultLanguageCode()))
            throw new NullPointerException("Default language is null or not loaded");
        return languageMap.get(getDefaultLanguageCode());
    }

    /** Builds the configured {@link I18n}. */
    public @NotNull I18n build() {
        return new I18n(getComponentAudience(), getLanguageMap(), getDefaultLanguage(), getStringInterpreter(), getComponentInterpreter(), isUsePlayerLanguage());
    }

}
