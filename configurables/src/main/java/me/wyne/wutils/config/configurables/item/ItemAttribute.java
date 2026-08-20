package me.wyne.wutils.config.configurables.item;

import org.jetbrains.annotations.NotNull;

/**
 * Config keys for the item attribute vocabulary, in {@code ItemConfigurable} registration
 * order — which is also application order at build time.
 */
public enum ItemAttribute {
    MATERIAL("material"),
    AMOUNT("amount"),
    NAME("name"),
    LORE("lore"),
    FLAGS("flags"),
    SKULL("skull"),
    SKULL64("skull64"),
    SKULL_PLAYER("skullPlayer"),
    UNBREAKABLE("unbreakable"),
    ENCHANTMENT("enchantment"),
    ENCHANTMENTS("enchantments"),
    ATTRIBUTE("attribute"),
    ATTRIBUTES("attributes"),
    GLOW("glow"),
    DURABILITY("durability"),
    DAMAGE("damage"),
    MODEL("model"),
    REPAIR_COST("repairCost"),
    POTION_COLOR("potionColor"),
    POTION_TYPE("potionType"),
    POTION_MODIFIER("potionModifier"),
    POTION_EFFECT("potionEffect"),
    POTION_EFFECTS("potionEffects"),
    ARMOR_COLOR("armorColor");

    private final String key;

    ItemAttribute(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }
}
