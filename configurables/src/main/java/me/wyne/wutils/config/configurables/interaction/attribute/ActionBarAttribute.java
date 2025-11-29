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

import java.util.List;

public class ActionBarAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public ActionBarAttribute(String key, List<String> value) {
        super(key, value);
    }

    public ActionBarAttribute(List<String> value) {
        super(InteractionAttribute.MESSAGE.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> audience.sendActionBar(component.get()));
    }

    public static final class Factory implements AttributeFactory<ActionBarAttribute> {
        @Override
        public ActionBarAttribute create(String key, ConfigurationSection config) {
            return new ActionBarAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
