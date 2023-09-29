package me.wyne.wutils.log;

import me.wyne.wutils.config.ConfigGenerator;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class AutoLogConfig implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean writeInfo = false;
    private boolean writeWarn = false;

    private boolean generateWrite = false;

    public AutoLogConfig(FileConfiguration config, String loggerName)
    {
        generateConfig(loggerName);
        readConfig(config, loggerName);
    }

    public AutoLogConfig(FileConfiguration config, String loggerName, boolean generateWrite)
    {
        this.generateWrite = generateWrite;
        generateConfig(loggerName);
        readConfig(config, loggerName);
    }

    public AutoLogConfig(FileConfiguration config, String loggerName, LogConfig defaultValues)
    {
        logInfo = defaultValues.logInfo();
        logWarn = defaultValues.logWarn();
        writeInfo = defaultValues.writeInfo();
        writeWarn = defaultValues.writeWarn();
        generateConfig(loggerName);
        readConfig(config, loggerName);
    }

    public AutoLogConfig(FileConfiguration config, String loggerName, LogConfig defaultValues, boolean generateWrite)
    {
        this.generateWrite = generateWrite;
        logInfo = defaultValues.logInfo();
        logWarn = defaultValues.logWarn();
        writeInfo = defaultValues.writeInfo();
        writeWarn = defaultValues.writeWarn();
        generateConfig(loggerName);
        readConfig(config, loggerName);
    }

    public AutoLogConfig(boolean logInfo, boolean logWarn, boolean writeInfo, boolean writeWarn)
    {
        this.logInfo = logInfo;
        this.logWarn = logWarn;
        this.writeInfo = writeInfo;
        this.writeWarn = writeWarn;
    }

    private void generateConfig(String loggerName)
    {
        if (!ConfigGenerator.global.isEmpty())
            ConfigGenerator.global.whitespace();

        for (Field field : AutoLogConfig.class.getDeclaredFields())
        {
            if (field.getName().contains("write") && !generateWrite)
                continue;
            if (field.getName().equals("generateWrite"))
                continue;

            try {
                ConfigGenerator.global.writeValue(loggerName + "-" + field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void readConfig(FileConfiguration config, String loggerName)
    {
        for (Field field : AutoLogConfig.class.getDeclaredFields())
        {
            if (field.getName().equals("generateWrite"))
                continue;

            field.setAccessible(true);
            try {
                field.setBoolean(this, config.getBoolean(loggerName + "-" + field.getName(), (Boolean) field.get(this)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean logInfo() {
        return logInfo;
    }

    @Override
    public boolean logWarn() {
        return logWarn;
    }

    @Override
    public boolean writeInfo() {
        return writeInfo;
    }

    @Override
    public boolean writeWarn() {
        return writeWarn;
    }
}
