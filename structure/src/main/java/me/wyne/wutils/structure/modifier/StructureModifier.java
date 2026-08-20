package me.wyne.wutils.structure.modifier;

/**
 * The canonical set of structure modifier config keys.
 *
 * <p>Each constant's {@link #getKey()} is the YAML key consumers write under a structure's
 * {@code modifiers} section, and enumeration order is the registration order of
 * {@link me.wyne.wutils.structure.Structure#STRUCTURE_MODIFIER_MAP}. Most keys are guessable from
 * the constant name, but {@link #EDIT_EXTINGUISH}'s key is the terse {@code "ex"}.</p>
 */
public enum StructureModifier {
    CLIPBOARD_ROTATE("rotate"),
    CLIPBOARD_FLIP("flip"),
    LOCATION_ALTITUDE("altitude"),
    REGION_EXPAND("expand"),
    REGION_CONTRACT("contract"),
    REGION_OUTSET("outset"),
    REGION_INSET("inset"),
    SNAPSHOT_ENTITIES("snapshotEntities"),
    SNAPSHOT_REMOVE_ENTITIES("snapshotRemoveEntities"),
    SNAPSHOT_BIOMES("snapshotBiomes"),
    SNAPSHOT_SOURCE_MASK("snapshotSourceMask"),
    PASTE_ENTITIES("pasteEntities"),
    PASTE_BIOMES("pasteBiomes"),
    PASTE_IGNORE_AIR("pasteIgnoreAir"),
    PASTE_SOURCE_MASK("pasteSourceMask"),
    EDIT_REPLACE("replace"),
    EDIT_SET("set"),
    EDIT_GROW("grow"),
    EDIT_SMOOTH("smooth"),
    EDIT_NATURALIZE("naturalize"),
    EDIT_FLORA("flora"),
    EDIT_FOREST("forest"),
    EDIT_BIOME("biome"),
    EDIT_DEFORM("deform"),
    EDIT_SNOW("snow"),
    EDIT_SNOW_IF_COLD("snowIfCold"),
    EDIT_ADAPT_SURFACE("adaptSurface"),
    EDIT_THAW("thaw"),
    EDIT_GREEN("green"),
    EDIT_EXTINGUISH("ex"),
    EDIT_BUTCHER("butcher"),
    EDIT_DELTREE("deltree"),
    EDIT_DROP_FLOATING("dropFloating");

    private final String key;

    StructureModifier(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
