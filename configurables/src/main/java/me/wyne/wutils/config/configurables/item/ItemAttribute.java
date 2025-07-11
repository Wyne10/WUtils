package me.wyne.wutils.config.configurables.item;

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

    ItemAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
