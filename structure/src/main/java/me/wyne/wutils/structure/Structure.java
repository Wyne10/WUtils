package me.wyne.wutils.structure;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.common.scheduler.Schedulers;
import me.wyne.wutils.common.world.WorldUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import com.google.common.base.Preconditions;
import me.wyne.wutils.config.configurables.AttributeConfigurable;
import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.structure.location.StructureLocation;
import me.wyne.wutils.structure.location.condition.LocationCondition;
import me.wyne.wutils.structure.modifier.RegionModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
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
import me.wyne.wutils.structure.modifier.edit.GreenEditModifier;
import me.wyne.wutils.structure.modifier.edit.NaturalizeEditModifier;
import me.wyne.wutils.structure.modifier.edit.ReplaceEditModifier;
import me.wyne.wutils.structure.modifier.edit.SetEditModifier;
import me.wyne.wutils.structure.modifier.edit.SmoothEditModifier;
import me.wyne.wutils.structure.modifier.edit.SnowEditModifier;
import me.wyne.wutils.structure.modifier.edit.ThawEditModifier;
import me.wyne.wutils.structure.region.StructureRegion;
import me.wyne.wutils.structure.region.condition.RegionCondition;
import me.wyne.wutils.structure.scheme.Scheme;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Structure implements CompositeConfigurable {

    public final static AttributeMap STRUCTURE_MODIFIER_MAP = new AttributeMap();

    static {
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
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SMOOTH.getKey(), new SmoothEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_NATURALIZE.getKey(), new NaturalizeEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_GREEN.getKey(), new GreenEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_BIOME.getKey(), new BiomeEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_FLORA.getKey(), new FloraEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_FOREST.getKey(), new ForestEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_THAW.getKey(), new ThawEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_SNOW.getKey(), new SnowEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_EXTINGUISH.getKey(), new ExtinguishEditModifier.Factory());
        STRUCTURE_MODIFIER_MAP.put(StructureModifier.EDIT_BUTCHER.getKey(), new ButcherEditModifier.Factory());
    }

    private String key;
    private StructureLocation location;
    private Scheme scheme;
    private StructureRegion region;
    private Set<LocationCondition> locationConditions;
    private Set<RegionCondition> regionConditions;
    private AttributeConfigurable structureModifiers;

    public Structure() {}

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

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
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
    private String conditionsToConfig(int depth, ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        for (LocationCondition locationCondition : locationConditions) {
            builder.append(locationCondition.toConfig(depth, configEntry));
        }
        for (RegionCondition regionCondition : regionConditions) {
            builder.append(regionCondition.toConfig(depth, configEntry));
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            throw new NullPointerException("Can't deserialize Structure object because config is null");
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

    public CompletableFuture<WorldStructure> create(long timeoutMillis, @Nullable StructureCancellationToken token) {
        return createWorldStructure(System.currentTimeMillis(), 0, timeoutMillis, token);
    }

    private CompletableFuture<WorldStructure> createWorldStructure(long startTime, long elapsedMillis, long timeoutMillis, @Nullable StructureCancellationToken token) {
        return getIntermediateStructure(startTime, elapsedMillis, timeoutMillis, token)
                .thenCompose(intermediate -> {
                    var regionModifiers = structureModifiers.getSet(RegionModifier.class);
                    var mutableRegion = intermediate.region();
                    for (RegionModifier regionModifier : regionModifiers) {
                        mutableRegion = regionModifier.apply(mutableRegion, intermediate.clipboardRegion());
                    }
                    final var protectedRegion = mutableRegion;
                    if (regionConditions.stream().anyMatch(condition -> !condition.isValid(intermediate, protectedRegion)))
                        return createWorldStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token);
                    else
                        return CompletableFuture.completedFuture(
                                new WorldStructure(
                                        intermediate,
                                        protectedRegion,
                                        structureModifiers.getAttributeContainer()
                                )
                        );
                })
                .whenComplete((intermediate, exception) -> {
                    if (exception != null)
                        PluginUtils.getLogger().error("Structure generation exception", exception);
                });
    }

    private CompletableFuture<IntermediateStructure> getIntermediateStructure(long startTime, long elapsedMillis, long timeoutMillis, @Nullable StructureCancellationToken token) {
        if (token != null && token.isCancelled())
            return CompletableFuture.failedFuture(new CancellationException("Structure generation has been cancelled"));
        if (elapsedMillis > timeoutMillis)
            return CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate intermediate structure in " + timeoutMillis + " ms"));
        return WorldUtils.getHighestLocationAtAsync(location.getLocation())
                .thenComposeAsync(highestLocation -> {
                    var clipboard = scheme.getClipboard();
                    var protectedRegion = this.region.getRegion(clipboard, highestLocation);
                    var editLocation = BukkitAdapter.adapt(highestLocation);
                    var region = Scheme.toWorld(clipboard, editLocation);
                    if (locationConditions.stream().anyMatch(condition -> !condition.isValid(highestLocation)))
                        return getIntermediateStructure(startTime, System.currentTimeMillis() - startTime, timeoutMillis, token);
                    else
                        return CompletableFuture.completedFuture(
                                new IntermediateStructure(
                                        getUniqueKey(highestLocation),
                                        clipboard,
                                        highestLocation,
                                        protectedRegion,
                                        region,
                                        elapsedMillis
                                )
                        );
                }, Schedulers.sync());
    }

    private String getUniqueKey(@NotNull Location location) {
        return (key + "-<x>x<y>y<z>z").replace("<x>", String.valueOf(location.getBlockX()))
                .replace("<y>", String.valueOf(location.getBlockY()))
                .replace("<z>", String.valueOf(location.getBlockZ()))
                .replace(".0", "")
                .replace(",", "-");
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

    public static final class Builder {

        private String key;
        private StructureLocation location;
        private Scheme scheme;
        private StructureRegion region;
        private final Set<LocationCondition> locationConditions = new LinkedHashSet<>();
        private final Set<RegionCondition> regionConditions = new LinkedHashSet<>();
        private final Map<String, Attribute<?>> modifiers = new LinkedHashMap<>();

        private Builder() {}

        public Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        public Builder location(@NotNull StructureLocation location) {
            this.location = location;
            return this;
        }

        public Builder scheme(@NotNull Scheme scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder region(@NotNull StructureRegion region) {
            this.region = region;
            return this;
        }

        public Builder locationCondition(@NotNull LocationCondition... conditions) {
            this.locationConditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public Builder regionCondition(@NotNull RegionCondition... conditions) {
            this.regionConditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public Builder modifier(@NotNull Attribute<?>... modifiers) {
            for (Attribute<?> modifier : modifiers)
                this.modifiers.put(modifier.getKey(), modifier);
            return this;
        }

        public Structure build() {
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
