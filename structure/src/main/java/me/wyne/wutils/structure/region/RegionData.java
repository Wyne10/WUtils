package me.wyne.wutils.structure.region;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Config-declared WorldGuard region metadata: id, whether the region is transient (not
 * persisted to WorldGuard's own storage), its flags, and its priority. Requires WorldGuard.
 */
public record RegionData(@NotNull String id, boolean isTransient, @NotNull Map<@NotNull Flag<?>, @NotNull Object> flags, int priority) implements CompositeConfigSerializable {

    /**
     * Applies {@link #flags()} and {@link #priority()} to {@code region}.
     */
    public void apply(@NotNull ProtectedRegion region) {
        region.setFlags(flags);
        region.setPriority(priority);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        var builder = new ConfigBuilder()
                .append(depth, "id", id)
                .appendIfNotEqual(depth, "transient", isTransient, false)
                .appendIfNotEqual(depth, "priority", priority, 0);
        if (!flags.isEmpty()) {
            var flagsBuilder = new ConfigBuilder();
            flags.forEach((flag, value) -> flagsBuilder.append(depth + 1, flag.getName(), marshal(flag, value)));
            builder.appendString(depth, "flags", flagsBuilder.buildNoTrail());
        }
        return builder.buildNoTrail();
    }

    @SuppressWarnings("unchecked")
    private static @NotNull Object marshal(@NotNull Flag<?> flag, @NotNull Object value) {
        return ((Flag<Object>) flag).marshal(value);
    }

    /**
     * Builds a {@link RegionData} from config. The {@code flags} section, if present, is
     * matched against WorldGuard's live flag registry via {@link Flags#fuzzyMatchFlag} and
     * parsed with each flag's own parser; an unknown flag name or an unparseable value
     * throws {@link IllegalArgumentException} naming the offending key.
     */
    public static final class Factory implements GenericFactory<RegionData> {
        @Override
        public @NotNull RegionData create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var id = section.getString("id");
            if (id == null)
                throw new IllegalArgumentException("Region id is missing at " + ConfigUtils.getPath(section, "id"));
            var isTransient = section.getBoolean("transient", false);
            Map<Flag<?>, Object> flags = new HashMap<>();
            var flagsSection = section.getConfigurationSection("flags");
            if (flagsSection != null) {
                for (String flagName : flagsSection.getKeys(false)) {
                    var parsed = parseFlag(flagName, flagsSection.getString(flagName, ""));
                    flags.put(parsed.getValue0(), parsed.getValue1());
                }
            }
            var priority = section.getInt("priority", 0);
            return new RegionData(id, isTransient, flags, priority);
        }

        private @NotNull Pair<@NotNull Flag<?>, @NotNull Object> parseFlag(@NotNull String key, @NotNull String value) {
            var flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), key);
            if (flag == null)
                throw new IllegalArgumentException("Unknown region flag '" + key + "'");
            FlagContext context = new FlagContext.FlagContextBuilder().setInput(value).build();
            try {
                return new Pair<>(flag, flag.parseInput(context));
            } catch (InvalidFlagFormat e) {
                throw new IllegalArgumentException("Invalid value '" + value + "' for region flag '" + key + "': " + e.getMessage(), e);
            }
        }
    }

}
