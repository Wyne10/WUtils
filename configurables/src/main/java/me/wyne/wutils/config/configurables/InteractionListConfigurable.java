package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import me.wyne.wutils.config.configurables.interaction.attribute.MessageAttribute;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wyne.wutils.config.configurables.InteractionConfigurable.INTERACTION_ATTRIBUTE_MAP;

public class InteractionListConfigurable implements CompositeConfigurable {

    private final List<InteractionConfigurable> interactions;

    public InteractionListConfigurable() {
        interactions = new LinkedList<>();
    }

    public InteractionListConfigurable(InteractionConfigurable... interactions) {
        this();
        addInteractions(interactions);
    }

    public InteractionListConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        for (int i = 0; i < interactions.size(); i++) {
            builder.appendComposite(depth, "interaction-" + i, interactions.get(i), configEntry);
        }
        return builder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject instanceof String) {
            interactions.clear();
            var container = new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP).toBuilder()
                    .with(new MessageAttribute(List.of((String) configObject)));
            var interaction = new InteractionConfigurable(container.buildImmutable());
            interactions.add(interaction);
            return;
        }
        if (configObject instanceof List<?>) {
            interactions.clear();
            var container = new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP).toBuilder()
                    .with(new MessageAttribute((List<String>) configObject));
            var interaction = new InteractionConfigurable(container.buildImmutable());
            interactions.add(interaction);
            return;
        }
        ConfigurationSection config = (ConfigurationSection) configObject;
        interactions.clear();
        config.getKeys(false).forEach(key ->
                interactions.add(new InteractionConfigurable(config.getConfigurationSection(key))));
    }

    public void send(CommandSender sender, InteractionAttributeContext context) {
        for (InteractionConfigurable interaction : interactions) {
            interaction.send(sender, context);
        }
    }

    public void send(CommandSender sender, @Nullable OfflinePlayer placeholderTarget, TextReplacement... textReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, textReplacements, new ComponentReplacement[]{});
        send(sender, context);
    }
    public void send(CommandSender sender, TextReplacement... textReplacements) {
        send(sender, I18n.toOfflinePlayer(sender), textReplacements);
    }

    public void sendComponent(CommandSender sender, @Nullable OfflinePlayer placeholderTarget, ComponentReplacement... componentReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, new TextReplacement[]{}, componentReplacements);
        send(sender, context);
    }

    public void sendComponent(CommandSender sender, ComponentReplacement... componentReplacements) {
        sendComponent(sender, I18n.toOfflinePlayer(sender), componentReplacements);
    }

    public InteractionListConfigurable addInteraction(InteractionConfigurable interaction) {
        interactions.add(interaction);
        return this;
    }

    public InteractionListConfigurable addInteractions(InteractionConfigurable... interactions) {
        this.interactions.addAll(Arrays.asList(interactions));
        return this;
    }

    public List<InteractionConfigurable> getInteractions() {
        return interactions;
    }
}
