package me.wyne.wutils.config;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A single top-level section of the generated config, holding its fields grouped by sub-section.
 *
 * <p>Sub-sections are emitted in the order they were first added, and fields in the order they were
 * added within their sub-section, so regenerating an unchanged config produces unchanged text.</p>
 */
public class ConfigSection {

    private final String section;
    /**
     * Key - Sub section
     */
    private final Map<String, Set<ConfigField>> fields = new LinkedHashMap<>();

    public ConfigSection(@NotNull String section)
    {
        this.section = section;
    }

    public ConfigSection(@NotNull String section, @NotNull Map<@NotNull String, @NotNull Set<@NotNull ConfigField>> fields) {
        this(section);
        this.fields.putAll(fields);
    }

    public void addField(@NotNull String subSection, @NotNull ConfigField field) {
        if (!fields.containsKey(subSection))
            fields.put(subSection, new LinkedHashSet<>());
        fields.get(subSection).add(field);
    }

    /**
     * Renders this section's YAML text, including its header line and every sub-section's fields.
     */
    public @NotNull String generateConfigSection() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(section.replaceAll(" ", "").toLowerCase()).append(":").append("\n");

        int i = 0;
        for (String subSection : fields.keySet()) {
            stringBuilder.append(generateSubSection(subSection));
            if (i < fields.size() - 1)
                stringBuilder.append("\n");
            i++;
        }

        return stringBuilder.toString();
    }

    private @NotNull String generateSubSection(@NotNull String subSection) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!subSection.isEmpty()) {
            stringBuilder.append("  ").append("### ").append(subSection).append("\n");
        }

        for (ConfigField configField : fields.get(subSection)) {
            stringBuilder.append(configField.generateConfigLine());
            if (stringBuilder.charAt(stringBuilder.length() - 1) != '\n')
                stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}
