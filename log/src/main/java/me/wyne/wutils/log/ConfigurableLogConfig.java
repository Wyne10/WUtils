package me.wyne.wutils.log;

import me.wyne.wutils.config.ConfigFieldRegistry;
import me.wyne.wutils.config.ConfigField;

import java.lang.reflect.Field;

public class ConfigurableLogConfig implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean writeInfo = false;
    private boolean writeWarn = false;

    private final String loggerName;
    private final ConfigFieldRegistry config;

    public ConfigurableLogConfig(String loggerName, ConfigFieldRegistry config)
    {
        this.loggerName = loggerName;
        this.config = config;
        generateConfig();
    }

    public ConfigurableLogConfig(String loggerName, ConfigFieldRegistry config, LogConfig baseConfig)
    {
        this.loggerName = loggerName;
        this.config = config;
        this.logInfo = baseConfig.logInfo();
        this.logWarn = baseConfig.logWarn();
        this.writeInfo = baseConfig.writeInfo();
        this.writeWarn = baseConfig.writeWarn();
        generateConfig();
    }

    private void generateConfig()
    {
        for (Field field : ConfigurableLogConfig.class.getDeclaredFields())
        {
            if (field.getType() != boolean.class)
                continue;

            try {
                config.registerConfigField("Log." + loggerName,
                        new ConfigField(this,
                                field,
                                loggerName + "-" + field.getName(),
                                String.valueOf((boolean)field.get(this)),
                                ""));
            } catch (IllegalAccessException e) {
                Log.global.exception("An exception occurred while trying to create ConfigurableLogConfig", e);
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
