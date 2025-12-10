package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.attribute.common.ColorAttribute;
import me.wyne.wutils.config.configurables.attribute.common.ColorsAttribute;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;

public class FireworkEffectAttribute extends ConfigurableAttribute<FireworkEffect> {

    public FireworkEffectAttribute(String key, FireworkEffect value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        builder.appendString(depth, getKey(), "");
        builder.append(depth + 1, "type", getValue().getType().name());
        builder.appendIfNotEqual(depth + 1, "flicker", getValue().hasFlicker(), false);
        builder.appendIfNotEqual(depth + 1, "trail", getValue().hasFlicker(), false);
        int i = 0;
        builder.appendString(depth + 1, "colors", "");
        for (Color color : getValue().getColors()) {
            builder.append(depth + 2, "color-" + i, color.asRGB());
            i++;
        }
        i = 0;
        builder.appendString(depth + 1, "fadeColors", "");
        for (Color color : getValue().getFadeColors()) {
            builder.append(depth + 2, "fade-" + i, color.asRGB());
            i++;
        }
        return builder.buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<FireworkEffectAttribute> {
        @Override
        public FireworkEffectAttribute fromSection(String key, ConfigurationSection section) {
            return new FireworkEffectAttribute(
                    key,
                    FireworkEffect.builder()
                            .flicker(section.getBoolean("flicker", false))
                            .trail(section.getBoolean("trail", false))
                            .withColor(new ColorsAttribute("colors", section).getValue().stream().map(AttributeBase::getValue).toList())
                            .withFade(new ColorsAttribute("fadeColors", section).getValue().stream().map(AttributeBase::getValue).toList())
                            .with(ConfigUtils.getByName(section.getString("type", "BALL"), FireworkEffect.Type.class))
                            .build()
            );
        }

        @Override
        public FireworkEffectAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string);
            return new FireworkEffectAttribute(
                    key,
                    FireworkEffect.builder()
                            .with(ConfigUtils.getByName(args.get(0, "BALL"), FireworkEffect.Type.class))
                            .flicker(Boolean.parseBoolean(args.get(1, "false")))
                            .trail(Boolean.parseBoolean(args.get(2, "false")))
                            .withColor(new ColorAttribute.Factory().fromString("color", args.get(3, "000000"), config).getValue())
                            .withFade(new ColorAttribute.Factory().fromString("fade", args.get(4, "000000"), config).getValue())
                            .build()
            );
        }
    }

}
