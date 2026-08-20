package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.BukkitComponentAudiences;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Sets the item's lore, resolving each configured line through {@code I18n.global.accessor(...)}
 * — so a line may be a translation key, MiniMessage markup or a PlaceholderAPI placeholder, with
 * {@code context}'s player deciding language and placeholder source. Branches on whether the
 * configured audience is a {@link BukkitComponentAudiences} and uses the deprecated bungee
 * component API if so.
 *
 * <p>Skips itself entirely when the configured list is empty, so {@code lore: []} does not clear
 * existing lore.</p>
 */
public class LoreAttribute extends ConfigurableAttribute<List<String>> implements ContextMetaAttribute {

    public LoreAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public LoreAttribute(@NotNull List<@NotNull String> value) {
        super(ItemAttribute.LORE.getKey(), value);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(@NotNull ItemMeta meta, @NotNull ItemAttributeContext context) {
        if (getValue().isEmpty()) return;
        if (I18n.global.getAudiences() instanceof BukkitComponentAudiences)
            meta.setLoreComponents(
                    getValue().stream()
                            .flatMap(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponentList(context.getPlayer(), context.getTextReplacements()).stream()
                                    .map(c -> c.replace(context.getComponentReplacements()).bungee())).toList()
            );
        else
            meta.lore(getValue().stream()
                    .flatMap(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponentList(context.getPlayer(), context.getTextReplacements()).stream()
                            .map(c -> c.replace(context.getComponentReplacements()).get())).toList()
            );
    }

    public static final class Factory implements AttributeFactory<LoreAttribute> {
        @Override
        public LoreAttribute create(String key, ConfigurationSection config) {
            return new LoreAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
