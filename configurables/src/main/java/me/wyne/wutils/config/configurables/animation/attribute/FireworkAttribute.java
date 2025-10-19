package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.Firework;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class FireworkAttribute extends ConfigurableAttribute<FireworkAttribute.FireworkData> implements ContextAnimationAttribute {

    public FireworkAttribute(String key, FireworkData value) {
        super(key, value);
    }

    public FireworkAttribute(FireworkData value) {
        super(AnimationAttribute.FIREWORK.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        var fireworkMeta = (FireworkMeta) new ItemStack(Material.FIREWORK_ROCKET).getItemMeta();
        fireworkMeta.addEffects(getValue().effects());
        return new Firework(
                LocationUtils.addRelative(context.getLocation(), getValue().offset()),
                fireworkMeta);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        builder.appendString(depth, getKey(), "");
        builder.appendIfNotEqual(depth + 1, "offset", getValue().offset(), VectorUtils.zero());
        builder.appendIfNotEqual(depth + 1, "power", getValue().power(), 1);
        if (getValue().effects().isEmpty()) return builder.buildNoSpace();
        int i = 0;
        builder.appendString(depth + 1, "effects", "");
        for (FireworkEffect effect : getValue().effects()) {
            builder.appendComposite(depth + 2, "effect-" + i, new FireworkEffectAttribute("effect-" + i, effect), configEntry);
            i++;
        }
        return builder.buildNoSpace();
    }

    public record FireworkData(Vector offset, int power, List<FireworkEffect> effects) {}

    public static final class Factory implements AttributeFactory {
        @Override
        public FireworkAttribute create(String key, ConfigurationSection config) {
            ConfigurationSection section = config.getConfigurationSection(key);
            return new FireworkAttribute(
                    key,
                    new FireworkData(
                            ConfigUtils.getVectorOrZero(section, "offset"),
                            section.getInt("power", 1),
                            new FireworkEffectsAttribute("effects", section).getValue().stream().map(AttributeBase::getValue).toList()
                    )
            );
        }
    }

}
