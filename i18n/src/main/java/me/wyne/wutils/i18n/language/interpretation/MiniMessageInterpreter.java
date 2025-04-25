package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiniMessageInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public MiniMessageInterpreter(StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public Component getComponent(Language language, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getString(language, path)));
    }

/*    @Override
    public Component getComponent(ILanguage language, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public Component getComponent(Language language, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getString(language, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path)));
    }

/*    @Override
    public Component getPlaceholderComponent(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path)));
    }

/*    @Override
    public Component getPlaceholderComponent(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public List<Component> getComponentList(Language language, String path) {
        return getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

/*    @Override
    public List<Component> getComponentList(ILanguage language, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

/*    @Override
    public List<Component> getPlaceholderComponentList(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

/*    @Override
    public List<Component> getPlaceholderComponentList(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers) {
        throw new NotImplementedException();
    }*/

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

}
