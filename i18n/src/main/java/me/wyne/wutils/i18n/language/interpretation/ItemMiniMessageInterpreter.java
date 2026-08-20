package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ComponentInterpreter} backed by Adventure's MiniMessage serializer, wrapping every result in
 * a non-italic empty root component to strip the default item-lore italicization (see
 * {@link ItemLegacyInterpreter}).
 */
public class ItemMiniMessageInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public ItemMiniMessageInterpreter(@NotNull StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getString(language, path)));
    }

/*    @Override
    public Component getComponent(ILanguage language, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getString(language, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path)));
    }

/*    @Override
    public Component getPlaceholderComponent(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path)));
    }

/*    @Override
    public Component getPlaceholderComponent(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

/*    @Override
    public List<Component> getComponentList(ILanguage language, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

/*    @Override
    public List<Component> getPlaceholderComponentList(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

/*    @Override
    public List<Component> getPlaceholderComponentList(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull String toString(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public @NotNull Component fromString(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

}
