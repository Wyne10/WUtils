package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.Material;

public class MaterialConfigurable implements CompositeConfigurable {

    private Material material;

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
    public void fromConfig(Object configObject) {
        this.material = Material.matchMaterial((String)configObject);
    }

    public Material getMaterial() {
        return material;
    }

}

