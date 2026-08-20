package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Whether the candidate location's biome is classified as mountainous.
 *
 * <p>{@code invert} follows the record's own polarity, not the config key's: {@code false}
 * means "must be in mountains" ({@code is-in-mountains: true} in config).</p>
 */
public record MountainsCondition(boolean invert) implements LocationCondition {
    public static final @NotNull Pattern MOUNTAINS_REGEX = Pattern.compile(".*(?:MOUNTAINS|HILLS).*");
    public static final @NotNull Set<@NotNull Biome> MOUNTAIN_BIOMES = Collections.unmodifiableSet(getMountainBiomes());

    /**
     * Builds the set of mountain biomes: every {@link Biome} whose name matches
     * {@link #MOUNTAINS_REGEX}, plus {@link Biome#ERODED_BADLANDS}, plus (on server version
     * 1.16.5 only) the edge biomes removed in later versions.
     */
    public static @NotNull Set<@NotNull Biome> getMountainBiomes() {
        Set<Biome> biomes = new HashSet<>();

        for (Biome biome : Biome.values()) {
            if (MOUNTAINS_REGEX.matcher(biome.name()).matches()) {
                biomes.add(biome);
            }
        }

        biomes.add(Biome.ERODED_BADLANDS);

        if (PluginUtils.getServerVersion() == 1165) {
            biomes.add(Biome.MOUNTAIN_EDGE);
            biomes.add(Biome.JUNGLE_EDGE);
            biomes.add(Biome.MODIFIED_JUNGLE_EDGE);
        }

        return biomes;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "is-in-mountains", !invert)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return MOUNTAIN_BIOMES.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
