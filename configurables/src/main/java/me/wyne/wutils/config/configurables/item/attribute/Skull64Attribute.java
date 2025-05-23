package me.wyne.wutils.config.configurables.item.attribute;

import com.destroystokyo.paper.profile.ProfileProperty;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Skull64Attribute extends MetaAttribute<String> {

    public Skull64Attribute(String value) {
        super(ItemAttribute.SKULL64.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        var profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", getValue()));
        ((SkullMeta)meta).setPlayerProfile(profile);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
