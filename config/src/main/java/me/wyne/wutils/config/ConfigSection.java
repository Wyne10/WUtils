package me.wyne.wutils.config;

import java.util.*;

public class ConfigSection {

    private static final String HEADER_FORMAT = "#####\n" +
                                              "# %s\n" +
                                              "#####\n\n";

    private final String section;
    /**
     * Key - Sub section
     */
    private final Map<String, Set<ConfigField>> fields = new HashMap<>();

    public ConfigSection(String section)
    {
        this.section = section;
    }

    public ConfigSection(String section, Map<String, Set<ConfigField>> fields)
    {
        this(section);
        this.fields.putAll(fields);
    }

    public void addField(String subSection, ConfigField field)
    {
        if (!fields.containsKey(subSection))
            fields.put(subSection, new LinkedHashSet<>());
        fields.get(subSection).add(field);
    }

    public String generateConfigSection()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(generateSectionHeader());
        stringBuilder.append(section.toLowerCase()).append(":").append("\n");

        for (String subSection : fields.keySet())
        {
            stringBuilder.append(generateSubSection(subSection));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    private String generateSubSection(String subSection)
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (!subSection.isEmpty()) {
            stringBuilder.append("  ").append("# ").append(subSection).append("\n");
        }

        for (ConfigField configField : fields.get(subSection))
        {
            stringBuilder.append(configField.generateConfigLine());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    private String generateSectionHeader()
    {
        return String.format(HEADER_FORMAT, section);
    }

}
