package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.item.ContextItemStackAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.config.configurables.item.ItemStackAttribute;
import me.wyne.wutils.config.configurables.item.attribute.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemConfigurableBase extends AttributeConfigurableBase {

    public final static AttributeMap ITEM_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MATERIAL.getKey(), new MaterialAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.AMOUNT.getKey(), new AmountAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.NAME.getKey(), new NameAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.LORE.getKey(), new LoreAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.FLAGS.getKey(), new FlagsAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL.getKey(), new SkullAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL64.getKey(), new Skull64Attribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL_PLAYER.getKey(), new SkullPlayerAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.UNBREAKABLE.getKey(), new UnbreakableAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENT.getKey(), new EnchantmentAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENTS.getKey(), EnchantmentsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTE.getKey(), new GenericAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTES.getKey(), GenericsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.GLOW.getKey(), new GlowAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DURABILITY.getKey(), new DurabilityAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DAMAGE.getKey(), new DamageAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MODEL.getKey(), new CustomModelDataAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.REPAIR_COST.getKey(), new RepairCostAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_COLOR.getKey(), new PotionColorAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_TYPE.getKey(), new PotionTypeAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_MODIFIER.getKey(), new PotionModifierAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECT.getKey(), new PotionEffectAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECTS.getKey(), PotionEffectsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ARMOR_COLOR.getKey(), new ArmorColorAttribute.Factory());
    }

    public ItemConfigurableBase(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public ItemConfigurableBase(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public ItemStack build(ItemAttributeContext context) {
        var itemStack = new ItemStack(Material.STONE);
        getAttributeContainer().getSet(ItemStackAttribute.class)
                .forEach(attribute -> {
                    if (attribute instanceof ContextItemStackAttribute)
                        ((ContextItemStackAttribute) attribute).apply(itemStack, context);
                    else
                        attribute.apply(itemStack);
                });
        return itemStack;
    }

    public ItemStack build(TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(null, textReplacements, new ComponentReplacement[]{});
        return build(context);
    }

    public ItemStack build(Player player, TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(player, textReplacements, new ComponentReplacement[]{});
        return build(context);
    }

    public ItemStack buildComponent(ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(null, new TextReplacement[]{}, componentReplacements);
        return build(context);
    }

    public ItemStack buildComponent(Player player, ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(player, new TextReplacement[]{}, componentReplacements);
        return build(context);
    }

    public ItemConfigurableBuilder toBuilder() {
        return new ItemConfigurableBuilder(this);
    }

    public static ItemConfigurableBuilder builder() {
        return new ItemConfigurableBuilder();
    }

}
