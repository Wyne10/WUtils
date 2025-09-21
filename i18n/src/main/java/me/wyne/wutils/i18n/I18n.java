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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class I18n {

    public static I18n global;
    public static final boolean IS_PLAIN_TEXT_UNAVAILABLE = Bukkit.getVersion().contains("1.16");

    private final ComponentAudiences audiences;

    private final Map<String, Language> languageMap = new HashMap<>();
    private final Table<Language, String, LocalizationAccessor> accessorCache = HashBasedTable.create();
    private final Language defaultLanguage;

    private final StringInterpreter stringInterpreter;
    private final ComponentInterpreter componentInterpreter;

    private final boolean usePlayerLanguage;

    public I18n(ComponentAudiences audiences, Language defaultLanguage, StringInterpreter stringInterpreter, ComponentInterpreter componentInterpreter, boolean usePlayerLanguage) {
        this.audiences = audiences;
        this.defaultLanguage = defaultLanguage;
        this.stringInterpreter = stringInterpreter;
        this.componentInterpreter = componentInterpreter;
        this.usePlayerLanguage = usePlayerLanguage;
    }

    public ComponentAudiences getAudiences() {
        return audiences;
    }

    public Map<String, Language> getLanguageMap() {
        return languageMap;
    }

    public Language getLanguage(@Nullable Locale locale) {
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

    public StringInterpreter string() {
        return stringInterpreter;
    }

    public ComponentInterpreter component() {
        return componentInterpreter;
    }

    public LocalizationAccessor accessor(String path) {
        return accessor(null, path);
    }

    public LocalizationAccessor accessor(@Nullable Object localeContainer, String path) {
        return accessor(I18n.toLocale(localeContainer), path);
    }

    public LocalizationAccessor accessor(@Nullable Locale locale, String path) {
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

    public static String reduceRawString(Collection<String> stringList) {
        return stringList.stream().reduce(I18n::reduceRawString).orElse("");
    }

    public static String reduceRawString(String s1, String s2) {
        return s1 + "\n" + s2;
    }

    public static Component reduceRawComponent(Collection<Component> componentList) {
        return componentList.stream().reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static Component reduceRawComponent(Component c1, Component c2) {
        return c1.append(Component.newline()).append(c2);
    }

    public static <T extends LocalizedString> String reduceString(Collection<T> stringList) {
        return stringList.stream().map(T::get).reduce(I18n::reduceRawString).orElse("");
    }

    public static <T extends LocalizedString> String reduceString(T s1, T s2) {
        return s1.get() + "\n" + s2.get();
    }

    public static <T extends LocalizedComponent> Component reduceComponent(Collection<T> componentList) {
        return componentList.stream().map(T::get).reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static <T extends LocalizedComponent> Component reduceComponent(T c1, T c2) {
        return c1.get().append(Component.newline()).append(c2.get());
    }

    public static List<String> ofRawStrings(Collection<String> paths, Function<String, String> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<Component> ofRawComponents(Collection<String> paths, Function<String, Component> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<LocalizedString> ofStrings(Collection<String> paths, Function<String, LocalizedString> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<LocalizedComponent> ofComponents(Collection<String> paths, Function<String, LocalizedComponent> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<Component> asComponents(Collection<LocalizedComponent> localizedComponents) {
        return localizedComponents.stream().map(LocalizedComponent::asComponent).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<LocalizedComponent> applyComponentReplacements(Collection<LocalizedComponent> localizedComponents, ComponentReplacement... replacements) {
        return localizedComponents.stream().map(lc -> lc.replace(replacements)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String applyTextReplacements(String string, TextReplacement ...textReplacements) {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    public static @Nullable <T> Player toPlayer(@Nullable T something) {
        if (something instanceof Player player)
            return player;
        return null;
    }

    public static @Nullable <T> Locale toLocale(@Nullable T localeContainer) {
        if (localeContainer instanceof Player player)
            return player.locale();
        return null;
    }

}
