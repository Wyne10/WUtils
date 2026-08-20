package me.wyne.wutils.i18n;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.access.ListLocalizationAccessor;
import me.wyne.wutils.i18n.language.access.LocalizationAccessor;
import me.wyne.wutils.i18n.language.access.StringLocalizationAccessor;
import me.wyne.wutils.i18n.language.component.*;
import me.wyne.wutils.i18n.language.interpretation.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Runtime entry point for a configured localization setup: holds the loaded {@link Language}s, the
 * default language, the configured interpreters, and the {@link ComponentAudiences} used to resolve a
 * player's locale.
 *
 * <p>Built via {@link BaseI18nBuilder} or {@link PluginI18nBuilder} rather than constructed directly.
 * The accessor cache uses Guava's {@code Table}, available transitively via paper-api rather than as a
 * declared dependency.</p>
 */
public class I18n {

    /**
     * Global {@link I18n} instance, left for consumers to assign so static helpers can reach a configured
     * localization setup without threading one through explicitly. Not initialized here — it stays
     * {@code null} until a consumer assigns it, and {@code Placeholder.replace(String, Component)},
     * {@code TextReplacement#asComponentReplacement()}, {@code ComponentReplacement#asTextReplacement()},
     * {@link #styleMap(Component, String)} and {@link #style(Component, String, String)} all dereference
     * it, throwing {@link NullPointerException} if it was never set.
     */
    public static @Nullable I18n global;

    private final ComponentAudiences audiences;

    private final Map<String, Language> languageMap = new HashMap<>();
    private final Table<Language, String, LocalizationAccessor> accessorCache = HashBasedTable.create();
    private final Language defaultLanguage;

    private final StringInterpreter stringInterpreter;
    private final ComponentInterpreter componentInterpreter;

    private final boolean usePlayerLanguage;

    /**
     * Constructs a fully configured {@link I18n}; prefer {@link BaseI18nBuilder} over calling this
     * directly.
     */
    public I18n(@NotNull ComponentAudiences audiences, @NotNull Map<@NotNull String, @NotNull Language> languageMap, @NotNull Language defaultLanguage, @NotNull StringInterpreter stringInterpreter, @NotNull ComponentInterpreter componentInterpreter, boolean usePlayerLanguage) {
        this.audiences = audiences;
        this.languageMap.putAll(languageMap);
        this.defaultLanguage = defaultLanguage;
        this.stringInterpreter = stringInterpreter;
        this.componentInterpreter = componentInterpreter;
        this.usePlayerLanguage = usePlayerLanguage;
    }

    public @NotNull ComponentAudiences getAudiences() {
        return audiences;
    }

    public @NotNull Map<@NotNull String, @NotNull Language> getLanguageMap() {
        return languageMap;
    }

    /**
     * Resolves the {@link Language} to use for {@code locale}, falling back to the default language when
     * per-player language selection is disabled, {@code locale} is {@code null} or has no language tag, or
     * no loaded language matches it.
     */
    public @NotNull Language getLanguage(@Nullable Locale locale) {
        if (!usePlayerLanguage)
            return defaultLanguage;
        if (locale == null)
            return defaultLanguage;
        if (locale.getLanguage().isEmpty())
            return defaultLanguage;
        if (!languageMap.containsKey(locale.getLanguage()))
            return defaultLanguage;
        return languageMap.get(locale.getLanguage());
    }

    /** Returns the configured {@link StringInterpreter}. */
    public @NotNull StringInterpreter string() {
        return stringInterpreter;
    }

    /** Returns the configured {@link ComponentInterpreter}. */
    public @NotNull ComponentInterpreter component() {
        return componentInterpreter;
    }

    /**
     * Returns the cached {@link LocalizationAccessor} for {@code path} in the default language, creating
     * and caching one — a {@link ListLocalizationAccessor} or {@link StringLocalizationAccessor} depending
     * on whether the underlying value is a list — if none exists yet.
     */
    public @NotNull LocalizationAccessor accessor(@NotNull String path) {
        return accessor(null, path);
    }

    /**
     * Returns the cached {@link LocalizationAccessor} for {@code path} in the language resolved from
     * {@code localeContainer} (typically a {@link Player}) via {@link #toLocale}.
     */
    public @NotNull LocalizationAccessor accessor(@Nullable Object localeContainer, @NotNull String path) {
        return accessor(I18n.toLocale(localeContainer), path);
    }

    /**
     * Returns the cached {@link LocalizationAccessor} for {@code path} in the language resolved from
     * {@code locale}. See {@link #getLanguage(Locale)} for the fallback rules.
     */
    public @NotNull LocalizationAccessor accessor(@Nullable Locale locale, @NotNull String path) {
        Language language = getLanguage(locale);
        if (accessorCache.contains(language, path))
            return accessorCache.get(language, path);
        LocalizationAccessor accessor;
        if (language.getStrings().isList(path))
            accessor = new ListLocalizationAccessor(path, language, string(), component(), audiences);
        else
            accessor = new StringLocalizationAccessor(path, language, string(), component(), audiences);
        accessorCache.put(language, path, accessor);
        return accessor;
    }

    /** Joins the strings with newlines, or returns an empty string for an empty collection. */
    public static @NotNull String reduceRawString(@NotNull Collection<@NotNull String> stringList) {
        return stringList.stream().reduce(I18n::reduceRawString).orElse("");
    }

    public static @NotNull String reduceRawString(@NotNull String s1, @NotNull String s2) {
        return s1 + "\n" + s2;
    }

    /** Joins the components with newlines, or returns an empty {@link Component} for an empty collection. */
    public static @NotNull Component reduceRawComponent(@NotNull Collection<@NotNull Component> componentList) {
        return componentList.stream().reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static @NotNull Component reduceRawComponent(@NotNull Component c1, @NotNull Component c2) {
        return c1.append(Component.newline()).append(c2);
    }

    /** Joins {@code T#get()} values with newlines, or returns an empty string for an empty collection. */
    public static @NotNull <T extends LocalizedString> String reduceString(@NotNull Collection<@NotNull T> stringList) {
        return stringList.stream().map(T::get).reduce(I18n::reduceRawString).orElse("");
    }

    public static @NotNull <T extends LocalizedString> String reduceString(@NotNull T s1, @NotNull T s2) {
        return s1.get() + "\n" + s2.get();
    }

    /** Joins {@code T#get()} values with newlines, or returns an empty {@link Component} for an empty collection. */
    public static @NotNull <T extends LocalizedComponent> Component reduceComponent(@NotNull Collection<@NotNull T> componentList) {
        return componentList.stream().map(T::get).reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static @NotNull <T extends LocalizedComponent> Component reduceComponent(@NotNull T c1, @NotNull T c2) {
        return c1.get().append(Component.newline()).append(c2.get());
    }

    /** Resolves each {@link LocalizedComponent} to its current {@link Component}. */
    public static @NotNull List<@NotNull Component> asComponents(@NotNull Collection<@NotNull LocalizedComponent> localizedComponents) {
        return localizedComponents.stream().map(LocalizedComponent::asComponent).collect(Collectors.toCollection(LinkedList::new));
    }

    /** Applies the given replacements to each {@link LocalizedComponent}, returning new instances. */
    public static @NotNull List<@NotNull LocalizedComponent> applyComponentReplacements(@NotNull Collection<@NotNull LocalizedComponent> localizedComponents, @NotNull ComponentReplacement... replacements) {
        return localizedComponents.stream().map(lc -> lc.replace(replacements)).collect(Collectors.toCollection(LinkedList::new));
    }

    /** Applies the given replacements to {@code string} in order. */
    public static @NotNull String applyTextReplacements(@NotNull String string, @NotNull TextReplacement ...textReplacements) {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    /** Returns {@code something} cast to {@link Player}, or {@code null} if it is not one. */
    public static @Nullable <T> Player toPlayer(@Nullable T something) {
        if (something instanceof Player player)
            return player;
        return null;
    }

    /** Returns {@code something} cast to {@link OfflinePlayer}, or {@code null} if it is not one. */
    public static @Nullable <T> OfflinePlayer toOfflinePlayer(@Nullable T something) {
        if (something instanceof OfflinePlayer player)
            return player;
        return null;
    }

    /** Returns the {@link Locale} of {@code localeContainer} if it is a {@link Player}, else {@code null}. */
    public static @Nullable <T> Locale toLocale(@Nullable T localeContainer) {
        if (localeContainer instanceof Player player)
            return player.locale();
        return null;
    }

    /** Serializes to the legacy {@code '&'}-formatted string. */
    public static @NotNull String serializeLegacy(@NotNull Component component) {
        return LegacyInterpreter.SERIALIZER.serialize(component);
    }

    /** Serializes to the legacy section-sign-formatted string. */
    public static @NotNull String serializeLegacySection(@NotNull Component component) {
        return LegacyInterpreter.SECTION_SERIALIZER.serialize(component);
    }

    /** Serializes to a Gson JSON string. */
    public static @NotNull String serializeGson(@NotNull Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    /** Serializes to plain text via the deprecated {@link PlainComponentSerializer}. */
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static @NotNull String serializePlain(@NotNull Component component) {
        return PlainComponentSerializer.plain().serialize(component);
    }

    /** Serializes to plain text. */
    public static @NotNull String serializePlainText(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /** Serializes to a MiniMessage string. */
    public static @NotNull String serializeMiniMessage(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /** Serializes to legacy Bungee {@link BaseComponent}s. */
    public static @NotNull BaseComponent[] serializeBungee(@NotNull Component component) {
        return BungeeComponentSerializer.get().serialize(component);
    }

    /** Deserializes a legacy {@code '&'}-formatted string. */
    public static @NotNull Component deserializeLegacy(@NotNull String string) {
        return LegacyInterpreter.SERIALIZER.deserialize(string);
    }

    /** Deserializes a legacy section-sign-formatted string. */
    public static @NotNull Component deserializeLegacySection(@NotNull String string) {
        return LegacyInterpreter.SECTION_SERIALIZER.deserialize(string);
    }

    /** Deserializes a Gson JSON string. */
    public static @NotNull Component deserializeGson(@NotNull String string) {
        return GsonComponentSerializer.gson().deserialize(string);
    }

    /** Deserializes plain text via the deprecated {@link PlainComponentSerializer}. */
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static @NotNull Component deserializePlain(@NotNull String string) {
        return PlainComponentSerializer.plain().deserialize(string);
    }

    /** Deserializes plain text. */
    public static @NotNull Component deserializePlainText(@NotNull String string) {
        return PlainTextComponentSerializer.plainText().deserialize(string);
    }

    /** Deserializes a MiniMessage string. */
    public static @NotNull Component deserializeMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    /** Deserializes legacy Bungee {@link BaseComponent}s. */
    public static @NotNull Component deserializeBungee(@NotNull BaseComponent[] components) {
        return BungeeComponentSerializer.get().deserialize(components);
    }

    /**
     * Builds a map from {@code key} and its serializer-suffixed variants ({@code -legacy}, {@code -parsed},
     * {@code -mm}, {@code -plain}, {@code -plainText}, {@code -gson}) to {@code component} rendered in each
     * corresponding format, plus {@code key} itself mapped through {@code interpreter}.
     */
    public static @NotNull Map<@NotNull String, @NotNull String> styleMap(@NotNull ComponentInterpreter interpreter, @NotNull Component component, @NotNull String key) {
        var result = new LinkedHashMap<String, String>();
        result.put(key, interpreter.toString(component));
        result.put(key + "-legacy", serializeLegacy(component));
        result.put(key + "-parsed", serializeLegacySection(component));
        result.put(key + "-mm", serializeMiniMessage(component));
        result.put(key + "-plain", serializePlain(component));
        result.put(key + "-plainText", serializePlainText(component));
        result.put(key + "-gson", serializeGson(component));
        return result;
    }

    /**
     * Looks up {@code value} — expected to be {@code key} or one of its suffixed variants, see
     * {@link #styleMap(ComponentInterpreter, Component, String)} — returning {@code null} if it matches
     * none of them.
     */
    public static @Nullable String style(@NotNull ComponentInterpreter interpreter, @NotNull Component component, @NotNull String key, @NotNull String value) {
        return styleMap(interpreter, component, key).get(value);
    }

    /**
     * Equivalent to {@link #styleMap(ComponentInterpreter, Component, String)} using {@link #global}'s
     * component interpreter.
     *
     * @throws NullPointerException if {@link #global} was never assigned
     */
    public static @NotNull Map<@NotNull String, @NotNull String> styleMap(@NotNull Component component, @NotNull String key) {
        return styleMap(I18n.global.component(), component, key);
    }

    /**
     * Equivalent to {@link #style(ComponentInterpreter, Component, String, String)} using {@link #global}'s
     * component interpreter.
     *
     * @throws NullPointerException if {@link #global} was never assigned
     */
    public static @Nullable String style(@NotNull Component component, @NotNull String key, @NotNull String value) {
        return style(I18n.global.component(), component, key, value);
    }

}
