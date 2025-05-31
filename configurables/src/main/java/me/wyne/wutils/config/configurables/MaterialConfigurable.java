package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class MaterialConfigurable implements CompositeConfigurable {

    private Material material = Material.STONE;

    public MaterialConfigurable() {}

    public MaterialConfigurable(String materialName) {
        fromConfig(materialName);
    }

    public MaterialConfigurable(Material material) {
        this.material = material;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return material.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.material = Material.matchMaterial((String) configObject);
    }

    public Material getMaterial() {
        return material;
    }

}

