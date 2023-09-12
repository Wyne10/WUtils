package me.wyne.wutils.log;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class LogRecode {

    public static LogRecode global;

    private Logger logger;
    private LogConfig config;
    private Executor fileWriteExecutor;
    private File logDirectory;

    public boolean isActive()
    {
        return logger != null && config != null;
    }

    public boolean isFileWriteActive()
    {
        return fileWriteExecutor != null && logDirectory != null;
    }

    public LogRecode(Logger logger, LogConfig logConfig)
    {
        this.logger = logger;
        this.config = logConfig;
    }

    public LogRecode(Logger logger, FileConfiguration config, String loggerName)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName);
    }

    public LogRecode(Logger logger, LogConfig logConfig, Executor logWriteExecutor, File logDirectory)
    {
        this.logger = logger;
        this.config = logConfig;
        this.fileWriteExecutor = logWriteExecutor;
        this.logDirectory = logDirectory;
    }

    public LogRecode(Logger logger, FileConfiguration config, String loggerName, Executor logWriteExecutor, File logDirectory)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName);
        this.fileWriteExecutor = logWriteExecutor;
        this.logDirectory = logDirectory;
    }


}
