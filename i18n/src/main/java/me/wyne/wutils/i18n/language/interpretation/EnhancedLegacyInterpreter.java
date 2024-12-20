package me.wyne.wutils.i18n.language.interpretation;

import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnhancedLegacyInterpreter extends LegacyInterpreter implements ComponentInterpreter {

    private final BaseInterpreter baseInterpreter;

    public EnhancedLegacyInterpreter(StringValidator stringValidator) {
        super(stringValidator);
        this.baseInterpreter = new BaseInterpreter(stringValidator);
    }

    @Override
    public void setStringValidator(StringValidator stringValidator) {
        this.stringValidator = stringValidator;
        baseInterpreter.setStringValidator(stringValidator);
    }

    @Override
    public Component getComponent(Language language, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getString(language, path)).build());
    }

    @Override
    public Component getComponent(Language language, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getString(language, path, textReplacements)).build());
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getPlaceholderString(language, player, path)).build());
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getPlaceholderString(language, player, path, textReplacements)).build());
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getPlaceholderString(language, player, path)).build());
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(baseInterpreter.getPlaceholderString(language, player, path, textReplacements)).build());
    }

    @Override
    public List<Component> getComponentList(Language language, String path) {
        return baseInterpreter.getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path) {
        return baseInterpreter.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path) {
        return baseInterpreter.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().buildComponent(s).build()))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public String toString(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}
