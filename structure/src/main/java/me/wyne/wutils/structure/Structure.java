package me.wyne.wutils.structure;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.scheduler.Schedulers;
import me.wyne.wutils.common.world.WorldUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import com.google.common.base.Preconditions;
import me.wyne.wutils.config.configurables.AttributeConfigurable;
import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.structure.location.StructureLocation;
import me.wyne.wutils.structure.location.condition.LocationCondition;
import me.wyne.wutils.structure.modifier.ClipboardModifier;
import me.wyne.wutils.structure.modifier.LocationModifier;
import me.wyne.wutils.structure.modifier.RegionModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import me.wyne.wutils.structure.modifier.clipboard.FlipClipboardModifier;
import me.wyne.wutils.structure.modifier.clipboard.RotateClipboardModifier;
import me.wyne.wutils.structure.modifier.location.AltitudeLocationModifier;
import me.wyne.wutils.structure.modifier.snapshot.BiomesSnapshotModifier;
import me.wyne.wutils.structure.modifier.snapshot.EntitiesSnapshotModifier;
import me.wyne.wutils.structure.modifier.snapshot.RemoveEntitiesSnapshotModifier;
import me.wyne.wutils.structure.modifier.snapshot.SourceMaskSnapshotModifier;
import me.wyne.wutils.structure.modifier.paste.BiomesPasteModifier;
import me.wyne.wutils.structure.modifier.paste.EntitiesPasteModifier;
import me.wyne.wutils.structure.modifier.paste.IgnoreAirPasteModifier;
import me.wyne.wutils.structure.modifier.paste.MaskSourcePasteModifier;
import me.wyne.wutils.structure.modifier.region.ContractRegionModifier;
import me.wyne.wutils.structure.modifier.region.ExpandRegionModifier;
import me.wyne.wutils.structure.modifier.region.InsetRegionModifier;
import me.wyne.wutils.structure.modifier.region.OutsetRegionModifier;
import me.wyne.wutils.structure.modifier.edit.BiomeEditModifier;
import me.wyne.wutils.structure.modifier.edit.ButcherEditModifier;
import me.wyne.wutils.structure.modifier.edit.DeformEditModifier;
import me.wyne.wutils.structure.modifier.edit.ExtinguishEditModifier;
import me.wyne.wutils.structure.modifier.edit.FloraEditModifier;
import me.wyne.wutils.structure.modifier.edit.ForestEditModifier;
import me.wyne.wutils.structure.modifier.edit.AdaptSurfaceEditModifier;
import me.wyne.wutils.structure.modifier.edit.DelTreeEditModifier;
import me.wyne.wutils.structure.modifier.edit.DropFloatingEditModifier;
import me.wyne.wutils.structure.modifier.edit.GreenEditModifier;
import me.wyne.wutils.structure.modifier.edit.GrowEditModifier;
import me.wyne.wutils.structure.modifier.edit.NaturalizeEditModifier;
import me.wyne.wutils.structure.modifier.edit.ReplaceEditModifier;
import me.wyne.wutils.structure.modifier.edit.SetEditModifier;
import me.wyne.wutils.structure.modifier.edit.SmoothEditModifier;
import me.wyne.wutils.structure.modifier.edit.SnowEditModifier;
import me.wyne.wutils.structure.modifier.edit.SnowIfColdEditModifier;
import me.wyne.wutils.structure.modifier.edit.ThawEditModifier;
import me.wyne.wutils.structure.region.StructureRegion;
import me.wyne.wutils.structure.region.condition.RegionCondition;
import me.wyne.wutils.structure.scheme.Scheme;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A config-declared recipe for placing a schematic-based structure: where to look for a valid
 * location, which {@link Scheme} supplies the clipboard, how the placement region is shaped, which
 * conditions a candidate location and region must satisfy, and which {@link StructureModifier}s to
 * apply along the way.
 *
 * <p>A {@code Structure} only describes the recipe; it holds no world state. Calling {@link #create}
 * runs the recipe and yields a {@link WorldStructure}, the placed (but not yet spawned) instance.</p>
 */
public class Structure implements CompositeConfigSerializable, ConfigDeserializable {

    /**
     * Canonical registry of every built-in {@link StructureModifier} key to its factory. This is the
     * order {@link Builder#build()} re-sorts a structure's configured modifiers into — modifiers
     * configured under a key present here are moved to this map's iteration order, and any
     * unrecognised (e.g. custom) modifier keys are appended afterward in their original order.
     */
    public final static AttributeMap STRUCTURE_MODIFIER_MAP = new AttributeMap();

    static {
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.CLIPBOARD_ROTATE.getKey(), new RotateClipboardModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.CLIPBOARD_FLIP.getKey(), new FlipClipboardModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.LOCATION_ALTITUDE.getKey(), new AltitudeLocationModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.SNAPSHOT_ENTITIES.getKey(), new EntitiesSnapshotModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.SNAPSHOT_REMOVE_ENTITIES.getKey(), new RemoveEntitiesSnapshotModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.SNAPSHOT_BIOMES.getKey(), new BiomesSnapshotModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.SNAPSHOT_SOURCE_MASK.getKey(), new SourceMaskSnapshotModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.PASTE_ENTITIES.getKey(), new EntitiesPasteModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.PASTE_BIOMES.getKey(), new BiomesPasteModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.PASTE_IGNORE_AIR.getKey(), new IgnoreAirPasteModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.PASTE_SOURCE_MASK.getKey(), new MaskSourcePasteModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.REGION_EXPAND.getKey(), new ExpandRegionModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.REGION_CONTRACT.getKey(), new ContractRegionModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.REGION_OUTSET.getKey(), new OutsetRegionModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.REGION_INSET.getKey(), new InsetRegionModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_REPLACE.getKey(), new ReplaceEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SET.getKey(), new SetEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_DEFORM.getKey(), new DeformEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_GROW.getKey(), new GrowEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SMOOTH.getKey(), new SmoothEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_NATURALIZE.getKey(), new NaturalizeEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_ADAPT_SURFACE.getKey(), new AdaptSurfaceEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_GREEN.getKey(), new GreenEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_BIOME.getKey(), new BiomeEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_FLORA.getKey(), new FloraEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_FOREST.getKey(), new ForestEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_THAW.getKey(), new ThawEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SNOW.getKey(), new SnowEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SNOW_IF_COLD.getKey(), new SnowIfColdEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_EXTINGUISH.getKey(), new ExtinguishEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_BUTCHER.getKey(), new ButcherEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_DELTREE.getKey(), new DelTreeEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_DROP_FLOATING.getKey(), new DropFloatingEditModifier.Factory());
    }

    private String key;
    private StructureLocation location;
    private Scheme scheme;
    private StructureRegion region;
    private Set<LocationCondition> locationConditions;
    private Set<RegionCondition> regionConditions;
    private AttributeConfigurable structureModifiers;

    public Structure() {}

    public Structure(ConfigurationSection section) {
        fromConfig(section);
    }

    private Structure(@NotNull String key,
                      @NotNull StructureLocation location,
                      @NotNull Scheme scheme,
                      @NotNull StructureRegion region,
                      @NotNull Set<LocationCondition> locationConditions,
                      @NotNull Set<RegionCondition> regionConditions,
                      @NotNull AttributeConfigurable structureModifiers) {
        this.key = key;
        this.location = location;
        this.scheme = scheme;
        this.region = region;
        this.locationConditions = locationConditions;
        this.regionConditions = regionConditions;
        this.structureModifiers = structureModifiers;
    }

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(@NotNull Structure source) {
        return new Builder().from(source);
    }

    @Contract("-> new")
    public @NotNull Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "key", key)
                .appendComposite(depth, "location", location, configEntry)
                .appendComposite(depth, "scheme", scheme, configEntry)
                .appendComposite(depth, "region", region, configEntry)
                .appendString(depth, "conditions", conditionsToConfig(depth + 1, configEntry))
                .appendComposite(depth, "modifiers", structureModifiers, configEntry)
                .build();
    }

    @Nullable
    private String conditionsToConfig(int depth, @NotNull ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        for (LocationCondition locationCondition : locationConditions) {
            builder.append(locationCondition.toConfig(depth, configEntry));
        }
        for (RegionCondition regionCondition : regionConditions) {
            builder.append(regionCondition.toConfig(depth, configEntry));
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    /**
     * Reads {@code key}, {@code location}, {@code scheme}, {@code region}, {@code conditions}, and
     * {@code modifiers} from the given {@link ConfigurationSection}, replacing this structure's
     * current state. A {@code null} argument is a no-op.
     */
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        var section = (ConfigurationSection) configObject;
        var key = section.getString("key", section.getName());
        var location = new StructureLocation.Factory().create("location", section);
        var scheme = new Scheme.Factory().create("scheme", section);
        var region = new StructureRegion.Factory().create("region", section);
        var locationConditions = LocationCondition.FACTORY_MAP.createAll(ConfigUtils.getConfigurationSection(section, "conditions"));
        var regionConditions = RegionCondition.FACTORY_MAP.createAll(ConfigUtils.getConfigurationSection(section, "conditions"));
        var structureModifiers = new AttributeConfigurable(new ImmutableAttributeContainer(STRUCTURE_MODIFIER_MAP), ConfigUtils.getConfigurationSection(section, "modifiers"));
        this.key = key;
        this.location = location;
        this.scheme = scheme;
        this.region = region;
        this.locationConditions = locationConditions;
        this.regionConditions = regionConditions;
        this.structureModifiers = structureModifiers;
    }

    /**
     * Resolves a valid location and region and returns the resulting {@link WorldStructure}, ready
     * to be {@link WorldStructure#spawn() spawned}.
     *
     * <p>Internally this retries: a candidate location is rejected if it fails any
     * {@link LocationCondition}, and a candidate region is rejected if it fails any
     * {@link RegionCondition}, in which case generation restarts from a fresh location. Retrying
     * continues until every condition passes, {@code timeoutMillis} (measured from the first call,
     * not per attempt) has elapsed, or {@code token} is cancelled — either of the latter two fails the
     * returned future. Location lookup hops onto the main thread via {@link Schedulers#sync()}
     * because it queries world height; the rest of generation, including modifier application, runs
     * on {@code executor}.</p>
     *
     * @param timeoutMillis the total time budget across all retries, in milliseconds
     */
    public @NotNull CompletableFuture<@NotNull WorldStructure> create(long timeoutMillis, @Nullable StructureCancellationToken token, @NotNull Executor executor) {
        return createWorldStructure(System.currentTimeMillis(), 0, timeoutMillis, token, executor);
    }

    private @NotNull CompletableFuture<@NotNull WorldStructure> createWorldStructure(long startTime, long elapsedMillis, long timeoutMillis, @Nullable StructureCancellationToken token, @NotNull Executor executor) {
        return getIntermediateStructure(startTime, elapsedMillis, timeoutMillis, token, executor)
                .thenComposeAsync(intermediate -> {
                    var regionModifiers = structureModifiers.getSet(RegionModifier.class);
                    var mutableRegion = intermediate.region();
                    for (RegionModifier regionModifier : regionModifiers) {
                        mutableRegion = regionModifier.apply(mutableRegion, intermediate.clipboardRegion());
                    }
                    final var protectedRegion = mutableRegion;
                    if (token != null && token.isCancelled())
                        return createWorldStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token, executor);
                    else if (regionConditions.stream().anyMatch(condition -> !condition.isValid(intermediate, protectedRegion)))
                        return createWorldStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token, executor);
                    else
                        return CompletableFuture.completedFuture(
                                new WorldStructure(
                                        intermediate,
                                        protectedRegion,
                                        structureModifiers.getAttributeContainer()
                                )
                        );
                }, executor);
    }

    private @NotNull CompletableFuture<@NotNull IntermediateStructure> getIntermediateStructure(long startTime, long elapsedMillis, long timeoutMillis, @Nullable StructureCancellationToken token, @NotNull Executor executor) {
        if (token != null && token.isCancelled())
            return CompletableFuture.failedFuture(new CancellationException("Structure generation has been cancelled"));
        if (elapsedMillis > timeoutMillis)
            return CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate intermediate structure in " + timeoutMillis + " ms"));
        return CompletableFuture.supplyAsync(location::getLocation, Schedulers.sync())
                .thenCompose(WorldUtils::getHighestLocationAtAsync)
                .thenComposeAsync(highestLocation -> {
                    if (token != null && token.isCancelled())
                        return getIntermediateStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token, executor);
                    highestLocation.add(0, 1, 0);
                    if (locationConditions.stream().anyMatch(condition -> !condition.isValid(highestLocation)))
                        return getIntermediateStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token, executor);
                    var clipboard = scheme.getClipboard();
                    var clipboardHolder = new ClipboardHolder(clipboard);
                    structureModifiers.getSet(ClipboardModifier.class)
                            .forEach(clipboardModifier -> clipboardModifier.apply(clipboardHolder));
                    var transform = clipboardHolder.getTransform();
                    Location placement = highestLocation;
                    for (LocationModifier locationModifier : structureModifiers.getSet(LocationModifier.class))
                        placement = locationModifier.apply(placement);
                    var protectedRegion = this.region.getRegion(clipboard, placement, transform);
                    var editLocation = BukkitAdapter.adapt(placement);
                    var region = Scheme.toWorld(clipboard, editLocation, transform);
                    region.setWorld(BukkitAdapter.adapt(placement.getWorld()));
                    return CompletableFuture.completedFuture(
                            new IntermediateStructure(
                                    getUniqueKey(placement),
                                    clipboard,
                                    placement,
                                    protectedRegion,
                                    region,
                                    transform,
                                    elapsedMillis
                            )
                    );
                }, executor);
    }

    /**
     * Builds this structure's config {@code key} with {@code <x>}, {@code <y>}, {@code <z>}
     * placeholders substituted for {@code location}'s block coordinates, then strips any character
     * outside {@code [A-Za-z0-9_,'+/-]} so the result is safe to use as a WorldGuard region id.
     */
    private @NotNull String getUniqueKey(@NotNull Location location) {
        return (key + "-<x>x<y>y<z>z")
                .replace("<x>", String.valueOf(location.getBlockX()))
                .replace("<y>", String.valueOf(location.getBlockY()))
                .replace("<z>", String.valueOf(location.getBlockZ()))
                .replaceAll("[^A-Za-z0-9_,'+/-]", "");
    }

    public String getKey() {
        return key;
    }

    public StructureLocation getLocation() {
        return location;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public StructureRegion getRegion() {
        return region;
    }

    public Set<LocationCondition> getLocationConditions() {
        return locationConditions;
    }

    public Set<RegionCondition> getRegionConditions() {
        return regionConditions;
    }

    public AttributeConfigurable getStructureModifiers() {
        return structureModifiers;
    }

    /**
     * Fluent builder for a {@link Structure}. {@link #build()} requires {@code key}, {@code location},
     * {@code scheme}, and {@code region} to have been set.
     */
    public static final class Builder {

        private String key;
        private StructureLocation location;
        private Scheme scheme;
        private StructureRegion region;
        private final Set<LocationCondition> locationConditions = new LinkedHashSet<>();
        private final Set<RegionCondition> regionConditions = new LinkedHashSet<>();
        private final Map<String, Attribute<?>> modifiers = new LinkedHashMap<>();

        private Builder() {}

        @Contract("_ -> this")
        public @NotNull Builder from(@NotNull Structure source) {
            if (source.key != null)
                this.key = source.key;
            if (source.location != null)
                this.location = source.location;
            if (source.scheme != null)
                this.scheme = source.scheme;
            if (source.region != null)
                this.region = source.region;
            if (source.locationConditions != null)
                this.locationConditions.addAll(source.locationConditions);
            if (source.regionConditions != null)
                this.regionConditions.addAll(source.regionConditions);
            if (source.structureModifiers != null)
                this.modifiers.putAll(source.structureModifiers.getAttributes());
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder location(@NotNull StructureLocation location) {
            this.location = location;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder scheme(@NotNull Scheme scheme) {
            this.scheme = scheme;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder region(@NotNull StructureRegion region) {
            this.region = region;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder locationCondition(@NotNull LocationCondition... conditions) {
            this.locationConditions.addAll(Arrays.asList(conditions));
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder regionCondition(@NotNull RegionCondition... conditions) {
            this.regionConditions.addAll(Arrays.asList(conditions));
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder modifier(@NotNull Attribute<?>... modifiers) {
            for (Attribute<?> modifier : modifiers)
                this.modifiers.put(modifier.getKey(), modifier);
            return this;
        }

        /**
         * Builds the {@link Structure}, re-sorting configured modifiers into
         * {@link #STRUCTURE_MODIFIER_MAP}'s registration order and appending any unrecognised
         * modifier keys afterward in the order they were added.
         *
         * @throws NullPointerException if {@code key}, {@code location}, {@code scheme}, or
         *                               {@code region} was never set
         */
        public @NotNull Structure build() {
            Preconditions.checkNotNull(key, "Structure key must be set");
            Preconditions.checkNotNull(location, "Structure location must be set");
            Preconditions.checkNotNull(scheme, "Structure scheme must be set");
            Preconditions.checkNotNull(region, "Structure region must be set");

            var containerBuilder = new AttributeContainerBuilder().with(STRUCTURE_MODIFIER_MAP);
            Map<String, Attribute<?>> remaining = new LinkedHashMap<>(modifiers);
            for (String canonicalKey : STRUCTURE_MODIFIER_MAP.getKeyMap().keySet()) {
                var modifier = remaining.remove(canonicalKey);
                if (modifier != null)
                    containerBuilder.with(modifier);
            }
            remaining.values().forEach(containerBuilder::with);

            return new Structure(
                    key,
                    location,
                    scheme,
                    region,
                    new LinkedHashSet<>(locationConditions),
                    new LinkedHashSet<>(regionConditions),
                    new AttributeConfigurable(containerBuilder.buildImmutable())
            );
        }
    }

}
