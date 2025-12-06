package me.wyne.wutils.config;

import java.util.*;

public class ConfigSection {

    private final String section;
    /**
     * Key - Sub section
     */
    private final Map<String, Set<ConfigField>> fields = new HashMap<>();

    public ConfigSection(String section)
    {
        this.section = section;
    }

    public ConfigSection(String section, Map<String, Set<ConfigField>> fields) {
        this(section);
        this.fields.putAll(fields);
    }

    public void addField(String subSection, ConfigField field) {
        if (!fields.containsKey(subSection))
            fields.put(subSection, new LinkedHashSet<>());
        fields.get(subSection).add(field);
    }

    public String generateConfigSection() {
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

    private String generateSubSection(String subSection) {
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
