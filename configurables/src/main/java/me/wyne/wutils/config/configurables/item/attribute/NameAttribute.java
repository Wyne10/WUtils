package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.BukkitComponentAudiences;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the item's display name, resolving the configured string through
 * {@code I18n.global.accessor(...)} — so it may be a translation key, MiniMessage markup or a
 * PlaceholderAPI placeholder, with {@code context}'s player deciding language and placeholder
 * source. Branches on whether the configured audience is a {@link BukkitComponentAudiences} and
 * uses the deprecated bungee component API if so. Defaults to an empty string when unconfigured.
 */
public class NameAttribute extends ConfigurableAttribute<String> implements ContextMetaAttribute {

    public NameAttribute(@NotNull String key, @NotNull String value) {
        super(key, value);
    }

    public NameAttribute(@NotNull String value) {
        super(ItemAttribute.NAME.getKey(), value);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(@NotNull ItemMeta meta, @NotNull ItemAttributeContext context) {
        if (I18n.global.getAudiences() instanceof BukkitComponentAudiences)
            meta.setDisplayNameComponent(I18n.global.accessor(context.getPlayer(), getValue()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).bungee());
        else
            meta.displayName(I18n.global.accessor(context.getPlayer(), getValue()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get());
    }

    public static final class Factory implements AttributeFactory<NameAttribute> {
        @Override
        public @NotNull NameAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new NameAttribute(key, config.getString(key, ""));
        }
    }

}
