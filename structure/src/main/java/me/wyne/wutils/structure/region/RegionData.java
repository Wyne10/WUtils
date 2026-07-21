package me.wyne.wutils.structure.region;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record RegionData(@NotNull String id, boolean isTransient, @NotNull Map<@NotNull Flag<?>, @NotNull Object> flags, int priority) implements CompositeConfigurable {

    public void apply(@NotNull ProtectedRegion region) {
        region.setFlags(flags);
        region.setPriority(priority);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        var builder = new ConfigBuilder();
        builder.append(depth, "id", id);
        builder.append(depth, "transient", priority);
        builder.append(depth, "priority", priority);
        builder.appendString(depth, "flags", "");
        flags.forEach((flag, value) -> {
            builder.append(depth + 1, flag.getName(), marshal(flag, value));
        });
        return builder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        throw new UnsupportedOperationException("RegionData is deserialized via RegionData.Factory");
    }

    @SuppressWarnings("unchecked")
    private static Object marshal(Flag<?> flag, Object value) {
        return ((Flag<Object>) flag).marshal(value);
    }

    public static final class Factory implements GenericFactory<RegionData> {
        @Override
        public RegionData create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var id = section.getString("id");
            if (id == null)
                throw new IllegalArgumentException("Region id is missing at " + ConfigUtils.getPath(section, "id"));
            var isTransient = section.getBoolean("transient", false);
            Map<Flag<?>, Object> flags = new HashMap<>();
            for (String flagName : section.getKeys(false)) {
                var parsed = parseFlag(flagName, section.getString(flagName, ""));
                flags.put(parsed.getValue0(), parsed.getValue1());
            }
            var priority = section.getInt("priority", 0);
            return new RegionData(id, isTransient, flags, priority);
        }

        private Pair<Flag<?>, Object> parseFlag(String key, String value) {
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
