package me.wyne.wutils.config;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;

import java.io.*;
import java.lang.reflect.Field;

public class ConfigGenerator {

    public final static ConfigGenerator global = new ConfigGenerator();

    private StringBuilder generatedText = new StringBuilder();

    @Contract("_ -> this")
    public ConfigGenerator writeString(String string)
    {
        generatedText.append(string);
        whitespace();
        return this;
    }

    @Contract("_, _ -> this")
    public ConfigGenerator writeValue(String path, Object value)
    {
        generatedText.append(path);
        generatedText.append(": ");
        generatedText.append(value.toString());
        whitespace();
        return this;
    }

    @Contract("_, _, _ -> this")
    public ConfigGenerator writeValue(String path, Object value, String comment)
    {
        generatedText.append(comment);
        whitespace();
        writeValue(path, value);
        return this;
    }

    @Contract("-> this")
    public ConfigGenerator whitespace() {
        generatedText.append("\n");
        return this;
    }

    @Contract("-> this")
    public ConfigGenerator clear() {
        generatedText = new StringBuilder();
        return this;
    }

    public static void writeConfigObject(Object object)
    {
        for(Field field : object.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(ConfigField.class))
            {
                field.setAccessible(true);
                ConfigField fieldAnnotation = field.getAnnotation(ConfigField.class);
                if (fieldAnnotation.generate() == false)
                    return;
                String path = fieldAnnotation.path().isEmpty() ? field.getName() : fieldAnnotation.path();
                Object value = null;
                try {
                    value = fieldAnnotation.value().isEmpty() ? field.get(object) : fieldAnnotation.value();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (fieldAnnotation.whitespace())
                    global.whitespace();

                if (!fieldAnnotation.comment().isEmpty())
                    global.writeValue(path, value, fieldAnnotation.comment());
                else
                    global.writeValue(path, value);
            }
        }
    }

    public void generate(File configFile)
    {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (config.getBoolean("config-generator") == true)
            return;
        else if (!config.contains("config-generator"))
            writeInitialData(configFile, config);
        else if (config.getBoolean("config-generator") == false)
        {
            overrideData(configFile);
            writeInitialData(configFile, config);
        }
    }

    private void writeInitialData(File configFile, FileConfiguration config)
    {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(configFile, true))) {
            if (!config.contains("config-generator"))
            {
                printWriter.println("\n\n# This value is used as an indicator of successful generation of the config.\n# Set the value to false to regenerate part of config that was generated automatically");
                printWriter.println("config-generator: true\n");
            }
            printWriter.println("# THIS PART OF CONFIG WAS GENERATED AUTOMATICALLY\n");
            printWriter.print(generatedText.toString());
            printWriter.println("\n# THIS PART OF CONFIG WAS GENERATED AUTOMATICALLY");
        } catch (IOException e) {
            Log.error("An exception occurred at config generation");
            Log.error(e.getMessage());
            Log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void overrideData(File configFile)
    {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
            StringBuilder fileContent = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equalsIgnoreCase("config-generator: false"))
                    break;
                fileContent.append(line).append(System.lineSeparator());
            }
            bufferedReader.close();
            fileContent.append("config-generator: true\n\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            Log.error("An exception occurred at config override");
            Log.error(e.getMessage());
            Log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
