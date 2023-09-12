package me.wyne.wutils.log;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public final class AutoLogConfig implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean writeInfo = false;
    private boolean writeWarn = false;

    public AutoLogConfig(FileConfiguration config, String loggerName)
    {
        generateConfig(config, loggerName);
        readConfig(config, loggerName);
    }

    public AutoLogConfig(boolean logInfo, boolean logWarn, boolean writeInfo, boolean writeWarn)
    {
        this.logInfo = logInfo;
        this.logWarn = logWarn;
        this.writeInfo = writeInfo;
        this.writeWarn = writeWarn;
    }

    private void generateConfig(FileConfiguration config, String loggerName)
    {
        for (Field field : AutoLogConfig.class.getFields())
        {
            if (!config.contains(loggerName + "-" + field.getName()))
                config.set(loggerName + "-" + field.getName(), false);
        }
    }

    private void readConfig(FileConfiguration config, String loggerName)
    {
        for (Field field : AutoLogConfig.class.getFields())
        {
            field.setAccessible(true);
            try {
                field.setBoolean(this, config.getBoolean(loggerName + "-" + field.getName()));
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
