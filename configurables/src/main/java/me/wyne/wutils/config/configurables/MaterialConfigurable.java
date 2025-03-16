package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.Configurable;
import org.bukkit.Material;

public class MaterialConfigurable implements Configurable {

    private Material material;

    public MaterialConfigurable(Material material)
    {
        this.material = material;
    }

    public MaterialConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
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

