package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code action} payload — sends each configured line as an action bar message to the audience.
 */
public class ActionBarAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public ActionBarAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public ActionBarAttribute(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.ACTION_BAR.getKey(), value);
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> audience.sendActionBar(component.get()));
    }

    public static final class Factory implements AttributeFactory<ActionBarAttribute> {
        @Override
        public @NotNull ActionBarAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new ActionBarAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
