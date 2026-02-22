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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class I18n {

    public static I18n global;

    private final ComponentAudiences audiences;

    private final Map<String, Language> languageMap = new HashMap<>();
    private final Table<Language, String, LocalizationAccessor> accessorCache = HashBasedTable.create();
    private final Language defaultLanguage;

    private final StringInterpreter stringInterpreter;
    private final ComponentInterpreter componentInterpreter;

    private final boolean usePlayerLanguage;

    public I18n(ComponentAudiences audiences, Map<String, Language> languageMap, Language defaultLanguage, StringInterpreter stringInterpreter, ComponentInterpreter componentInterpreter, boolean usePlayerLanguage) {
        this.audiences = audiences;
        this.languageMap.putAll(languageMap);
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

    public static List<Component> asComponents(Collection<LocalizedComponent> localizedComponents) {
        return localizedComponents.stream().map(LocalizedComponent::asComponent).collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<LocalizedComponent> applyComponentReplacements(Collection<LocalizedComponent> localizedComponents, ComponentReplacement... replacements) {
        return localizedComponents.stream().map(lc -> lc.replace(replacements)).collect(Collectors.toCollection(LinkedList::new));
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

    public static @Nullable <T> OfflinePlayer toOfflinePlayer(@Nullable T something) {
        if (something instanceof OfflinePlayer player)
            return player;
        return null;
    }

    public static @Nullable <T> Locale toLocale(@Nullable T localeContainer) {
        if (localeContainer instanceof Player player)
            return player.locale();
        return null;
    }

    public static String serializeLegacy(Component component) {
        return LegacyInterpreter.SERIALIZER.serialize(component);
    }

    public static String serializeLegacySection(Component component) {
        return LegacyInterpreter.SECTION_SERIALIZER.serialize(component);
    }

    public static String serializeGson(Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static String serializePlain(Component component) {
        return PlainComponentSerializer.plain().serialize(component);
    }

    public static String serializePlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String serializeMiniMessage(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static BaseComponent[] serializeBungee(Component component) {
        return BungeeComponentSerializer.get().serialize(component);
    }

    public static Component deserializeLegacy(String string) {
        return LegacyInterpreter.SERIALIZER.deserialize(string);
    }

    public static Component deserializeLegacySection(String string) {
        return LegacyInterpreter.SECTION_SERIALIZER.deserialize(string);
    }

    public static Component deserializeGson(String string) {
        return GsonComponentSerializer.gson().deserialize(string);
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static Component deserializePlain(String string) {
        return PlainComponentSerializer.plain().deserialize(string);
    }

    public static Component deserializePlainText(String string) {
        return PlainTextComponentSerializer.plainText().deserialize(string);
    }

    public static Component deserializeMiniMessage(String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    public static Component deserializeBungee(BaseComponent[] components) {
        return BungeeComponentSerializer.get().deserialize(components);
    }

    public static Map<String, String> styleMap(ComponentInterpreter interpreter, Component component, String key) {
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

    public static String style(ComponentInterpreter interpreter, Component component, String key, String value) {
        return styleMap(interpreter, component, key).get(value);
    }

    public static Map<String, String> styleMap(Component component, String key) {
        return styleMap(I18n.global.component(), component, key);
    }

    public static String style( Component component, String key, String value) {
        return style(I18n.global.component(), component, key, value);
    }

}
