package me.wyne.wutils.structure.modifier;

public enum StructureModifier {
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
    EDIT_SMOOTH("smooth"),
    EDIT_NATURALIZE("naturalize"),
    EDIT_FLORA("flora"),
    EDIT_FOREST("forest"),
    EDIT_BIOME("biome"),
    EDIT_DEFORM("deform"),
    EDIT_SNOW("snow"),
    EDIT_THAW("thaw"),
    EDIT_GREEN("green"),
    EDIT_EXTINGUISH("ex"),
    EDIT_BUTCHER("butcher");

    private final String key;

    StructureModifier(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
