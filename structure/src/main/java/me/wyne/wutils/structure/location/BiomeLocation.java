package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.world.BiomePreset;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A location near a biome, drawn from an {@code origin} {@link RandomLocation}.
 *
 * <p>{@link #getLocation()} draws an origin point, locates the nearest matching biome within
 * {@code radius} via {@link org.bukkit.World#locateNearestBiome World#locateNearestBiome}, then tries up to {@value #BOUNDS_ATTEMPTS}
 * random points at a {@code near} distance from it, falling back to the plain origin point if
 * none lands inside the origin range (or if no matching biome is found at all). {@code invert}
 * selects a biome <em>not</em> in {@code biomes} instead of one that is.</p>
 *
 * <p>{@link org.bukkit.World#locateNearestBiome World#locateNearestBiome} is expensive and must be called on the main thread.</p>
 */
public record BiomeLocation(@NotNull RandomLocation origin, @NotNull Set<@NotNull Biome> biomes, boolean invert,
                            int radius, @NotNull ClosedIntRange near,
                            @Nullable List<@NotNull String> presets) implements StructureLocation {

    public static final int DEFAULT_RADIUS = 6400;
    private static final int BOUNDS_ATTEMPTS = 16;

    @Override
    public @NotNull Location getLocation() {
        Location from = origin.getLocation();
        Biome target = pickBiome();
        if (target == null)
            return from;
        Location found = from.getWorld().locateNearestBiome(from, target, radius, 64);
        if (found == null)
            return from;
        for (int attempt = 0; attempt < BOUNDS_ATTEMPTS; attempt++) {
            Location candidate = LocationUtils.getRandomPointNear(found, near.getRandom());
            if (origin.withinBounds(candidate))
                return candidate;
        }
        return from;
    }

    /**
     * Picks a random biome from {@code biomes} (or its complement, if {@code invert}), or
     * {@code null} if the resulting pool is empty.
     */
    private @Nullable Biome pickBiome() {
        List<Biome> pool;
        if (invert) {
            pool = new ArrayList<>();
            for (Biome biome : Biome.values()) {
                if (!biomes.contains(biome))
                    pool.add(biome);
            }
        } else {
            pool = new ArrayList<>(biomes);
        }
        if (pool.isEmpty())
            return null;
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        var builder = new ConfigBuilder()
                .append(depth, "world", origin.range().getWorld().getName())
                .append(depth, "range", origin.range())
                .append(depth, "except", origin.except());
        if (presets != null) {
            builder.appendCollection(depth, "biome-preset", presets);
        } else {
            List<String> keys = biomes.stream()
                    .map(biome -> biome.getKey().toString())
                    .toList();
            builder.appendCollection(depth, invert ? "far-biome" : "near-biome", keys);
        }
        return builder
                .append(depth, "radius", radius)
                .append(depth, "near", near)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public @NotNull StructureLocation create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var origin = (RandomLocation) new RandomLocation.Factory().create(key, config);
            int radius = section.getInt("radius", DEFAULT_RADIUS);
            ClosedIntRange near = section.contains("near")
                    ? ClosedIntRange.getIntRange(section.getString("near", "10..200"))
                    : new ClosedIntRange(10, 200);

            if (section.contains("biome-preset")) {
                List<String> presets = section.getStringList("biome-preset");
                return new BiomeLocation(origin, BiomePreset.resolve(presets), false, radius, near, presets);
            }

            boolean invert = section.contains("far-biome");
            Set<Biome> biomes = ConfigUtils.getKeyedEnumSet(section, invert ? "far-biome" : "near-biome", Biome.class);
            return new BiomeLocation(origin, biomes, invert, radius, near, null);
        }
    }
}
