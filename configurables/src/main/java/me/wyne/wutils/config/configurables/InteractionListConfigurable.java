package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wyne.wutils.config.configurables.InteractionConfigurable.INTERACTION_ATTRIBUTE_MAP;

/**
 * A sequence of {@link InteractionConfigurable}s, all fired together by {@link #send}.
 *
 * <p>{@link #fromConfig} accepts three shapes: a bare string or a string list each build a single
 * interaction carrying only a {@link MessageAttribute}, built through
 * {@link ImmutableAttributeContainer#toBuilder()} rather than {@code fromConfig} — so, unlike a
 * section-built interaction, that shorthand interaction has no {@code root} attribute. A section
 * builds one interaction per child key.</p>
 */
public class InteractionListConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private final List<InteractionConfigurable> interactions;

    public InteractionListConfigurable() {
        interactions = new LinkedList<>();
    }

    public InteractionListConfigurable(@NotNull InteractionConfigurable... interactions) {
        this();
        addInteractions(interactions);
    }

    public InteractionListConfigurable(@NotNull ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        for (int i = 0; i < interactions.size(); i++) {
            builder.appendComposite(depth, "interaction-" + i, interactions.get(i), configEntry);
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
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

    public void send(@NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        for (InteractionConfigurable interaction : interactions) {
            interaction.send(sender, context);
        }
    }

    public void send(@NotNull CommandSender sender, @Nullable OfflinePlayer placeholderTarget, @NotNull TextReplacement... textReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, textReplacements, new ComponentReplacement[]{});
        send(sender, context);
    }
    public void send(@NotNull CommandSender sender, @NotNull TextReplacement... textReplacements) {
        send(sender, I18n.toOfflinePlayer(sender), textReplacements);
    }

    public void sendComponent(@NotNull CommandSender sender, @Nullable OfflinePlayer placeholderTarget, @NotNull ComponentReplacement... componentReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, new TextReplacement[]{}, componentReplacements);
        send(sender, context);
    }

    public void sendComponent(@NotNull CommandSender sender, @NotNull ComponentReplacement... componentReplacements) {
        sendComponent(sender, I18n.toOfflinePlayer(sender), componentReplacements);
    }

    public @NotNull InteractionListConfigurable addInteraction(@NotNull InteractionConfigurable interaction) {
        interactions.add(interaction);
        return this;
    }

    public @NotNull InteractionListConfigurable addInteractions(@NotNull InteractionConfigurable... interactions) {
        this.interactions.addAll(Arrays.asList(interactions));
        return this;
    }

    public @NotNull List<@NotNull InteractionConfigurable> getInteractions() {
        return interactions;
    }
}
