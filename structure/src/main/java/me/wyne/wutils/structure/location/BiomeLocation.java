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

public record BiomeLocation(@NotNull RandomLocation origin, @NotNull Set<@NotNull Biome> biomes, boolean invert,
                            int radius, @NotNull ClosedIntRange near,
                            @Nullable List<@NotNull String> presets) implements StructureLocation {

    public static final int DEFAULT_RADIUS = 6400;

    @Override
    public @NotNull Location getLocation() {
        Location from = origin.getLocation();
        Biome target = pickBiome();
        if (target == null)
            return from;
        Location found = from.getWorld().locateNearestBiome(from, target, radius, 64);
        if (found == null)
            return from;
        return LocationUtils.getRandomPointNear(found, near.getRandom());
    }

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
        public StructureLocation create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var origin = (RandomLocation) new RandomLocation.Factory().create(key, config);
            int radius = section.getInt("radius", DEFAULT_RADIUS);
            ClosedIntRange near = section.contains("near")
                    ? ClosedIntRange.getIntRange(section.getString("near", "10..100"))
                    : new ClosedIntRange(0, 0);

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
