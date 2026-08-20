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
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A single firework effect (type, flicker/trail, colors, fade colors), nested under a
 * {@link FireworkAttribute}'s {@code effects} section.
 *
 * <p>{@link #toConfig} renders only the effect's body, leading with a newline, because it is always
 * written through {@link ConfigBuilder#appendComposite} — which has already emitted the key.</p>
 */
public class FireworkEffectAttribute extends ConfigurableAttribute<FireworkEffect> {

    public FireworkEffectAttribute(@NotNull String key, @NotNull FireworkEffect value) {
        super(key, value);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        builder.append(depth, "type", getValue().getType().name());
        builder.appendIfNotEqual(depth, "flicker", getValue().hasFlicker(), false);
        builder.appendIfNotEqual(depth, "trail", getValue().hasTrail(), false);
        appendColors(builder, depth, "colors", "color-", getValue().getColors());
        appendColors(builder, depth, "fadeColors", "fade-", getValue().getFadeColors());
        return builder.buildNoTrail();
    }

    // An empty section would reload as a null ConfigurationSection and NPE in CompositeAttribute.
    private void appendColors(@NotNull ConfigBuilder builder, int depth, @NotNull String path,
                              @NotNull String prefix, @NotNull List<@NotNull Color> colors) {
        if (colors.isEmpty())
            return;
        builder.appendString(depth, path, "");
        int i = 0;
        for (Color color : colors) {
            builder.append(depth + 1, prefix + i, ColorAttribute.toHex(color));
            i++;
        }
    }

    public static final class Factory implements CompositeAttributeFactory<FireworkEffectAttribute> {
        @Override
        public @NotNull FireworkEffectAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
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
        public @NotNull FireworkEffectAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
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
