package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * A location near a structure, drawn from an {@code origin} {@link RandomLocation}.
 *
 * <p>Follows the same pattern as {@link BiomeLocation}: draw an origin point, locate the
 * nearest matching structure within {@code radius} via {@link org.bukkit.World#locateNearestStructure World#locateNearestStructure},
 * then try up to {@value #BOUNDS_ATTEMPTS} random points at a {@code near} distance from it,
 * falling back to the plain origin point if none lands inside the origin range (or if no
 * matching structure is found at all). {@code invert} selects a structure type <em>not</em>
 * in {@code structures} instead of one that is.</p>
 *
 * <p>{@link org.bukkit.World#locateNearestStructure World#locateNearestStructure} is expensive and must be called on the main thread.</p>
 */
public record NearestStructureLocation(@NotNull RandomLocation origin, @NotNull Set<@NotNull StructureType> structures,
                                       boolean invert, int radius, boolean findUnexplored,
                                       @NotNull ClosedIntRange near) implements StructureLocation {

    public static final int DEFAULT_RADIUS = 100;
    public static final boolean DEFAULT_FIND_UNEXPLORED = false;
    private static final int BOUNDS_ATTEMPTS = 16;

    @Override
    public @NotNull Location getLocation() {
        Location from = origin.getLocation();
        StructureType target = pickStructure();
        if (target == null)
            return from;
        Location found = from.getWorld().locateNearestStructure(from, target, radius, findUnexplored);
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
     * Picks a random structure type from {@code structures} (or its complement, if
     * {@code invert}), or {@code null} if the resulting pool is empty.
     */
    private @Nullable StructureType pickStructure() {
        List<StructureType> pool;
        if (invert) {
            pool = new ArrayList<>();
            for (StructureType type : StructureType.getStructureTypes().values()) {
                if (!structures.contains(type))
                    pool.add(type);
            }
        } else {
            pool = new ArrayList<>(structures);
        }
        if (pool.isEmpty())
            return null;
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        List<String> names = structures.stream()
                .map(StructureType::getName)
                .toList();
        return new ConfigBuilder()
                .append(depth, "world", origin.range().getWorld().getName())
                .append(depth, "range", origin.range())
                .append(depth, "except", origin.except())
                .appendCollection(depth, invert ? "far-structure" : "near-structure", names)
                .append(depth, "radius", radius)
                .appendIfNotEqual(depth, "find-unexplored", findUnexplored, DEFAULT_FIND_UNEXPLORED)
                .append(depth, "near", near)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public @NotNull StructureLocation create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var origin = (RandomLocation) new RandomLocation.Factory().create(key, config);
            int radius = section.getInt("radius", DEFAULT_RADIUS);
            boolean findUnexplored = section.getBoolean("find-unexplored", DEFAULT_FIND_UNEXPLORED);
            ClosedIntRange near = section.contains("near")
                    ? ClosedIntRange.getIntRange(section.getString("near", "10..100"))
                    : new ClosedIntRange(10, 100);

            boolean invert = section.contains("far-structure");
            Set<StructureType> structures = parseStructures(section, invert ? "far-structure" : "near-structure");
            return new NearestStructureLocation(origin, structures, invert, radius, findUnexplored, near);
        }

        private static @NotNull Set<@NotNull StructureType> parseStructures(@NotNull ConfigurationSection section, @NotNull String path) {
            Map<String, StructureType> registry = StructureType.getStructureTypes();
            return ConfigUtils.getStringList(section, path).stream()
                    .map(name -> registry.get(name.toLowerCase(Locale.ENGLISH)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}
