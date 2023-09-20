package me.wyne.wutils.log;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    public static Log global;

    private final Logger logger;
    private final LogConfig config;
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

    public Log(Logger logger, LogConfig logConfig)
    {
        this.logger = logger;
        this.config = logConfig;
    }

    public Log(Logger logger, FileConfiguration config, String loggerName)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName, false);
    }

    public Log(Logger logger, FileConfiguration config, String loggerName, LogConfig defaultValues)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName, defaultValues, false);
    }

    public Log(Logger logger, LogConfig logConfig, Executor logWriteExecutor, File logDirectory)
    {
        this.logger = logger;
        this.config = logConfig;
        this.fileWriteExecutor = logWriteExecutor;
        this.logDirectory = logDirectory;
        deleteOlderLogs();
    }

    public Log(Logger logger, FileConfiguration config, String loggerName, Executor logWriteExecutor, File logDirectory)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName, true);
        this.fileWriteExecutor = logWriteExecutor;
        this.logDirectory = logDirectory;
        deleteOlderLogs();
    }

    public Log(Logger logger, FileConfiguration config, String loggerName, LogConfig defaultValues, Executor logWriteExecutor, File logDirectory)
    {
        this.logger = logger;
        this.config = new AutoLogConfig(config, loggerName, defaultValues, true);
        this.fileWriteExecutor = logWriteExecutor;
        this.logDirectory = logDirectory;
        deleteOlderLogs();
    }

    public boolean info(String message)
    {
        if (isActive() && config.logInfo())
        {
            logger.info(message);
            writeLog(Level.INFO, message);
            return true;
        }
        return false;
    }

    public boolean warn(String message)
    {
        if (isActive() && config.logWarn())
        {
            logger.warning(message);
            writeLog(Level.WARNING, message);
            return true;
        }
        return false;
    }

    public boolean error(String message)
    {
        if (isActive())
        {
            logger.severe(message);
            writeLog(Level.SEVERE, message);
            return true;
        }
        return false;
    }

    public boolean exception(String message, Throwable exception)
    {
        if (isActive())
        {
            error(message);
            error(exception.getMessage());
            error(ExceptionUtils.getStackTrace(exception));
            return true;
        }
        return false;
    }

    public boolean log(LogMessage logMessage)
    {
        if (isActive())
        {
            if (logMessage.getLevel() == Level.INFO && config.logInfo() == false)
                return false;
            if (logMessage.getLevel() == Level.WARNING && config.logWarn() == false)
                return false;

            logger.log(logMessage.getLevel(), logMessage.getMessage());
            writeLog(logMessage.getLevel(), logMessage.getMessage());
            return true;
        }
        return false;
    }

    public boolean log(LogMessage logMessage, String splitRegex)
    {
        if (isActive())
        {
            if (logMessage.getLevel() == Level.INFO && config.logInfo() == false)
                return false;
            if (logMessage.getLevel() == Level.WARNING && config.logWarn() == false)
                return false;

            for (String message : logMessage.getMessage().split(splitRegex))
            {
                logger.log(logMessage.getLevel(), message);
                writeLog(logMessage.getLevel(), message);
            }
            return true;
        }
        return false;
    }

    public void writeLog(Level level, String log)
    {
        if (!isFileWriteActive())
            return;
        if (level == Level.INFO && config.writeInfo() == false)
            return;
        if (level == Level.WARNING && config.writeInfo() == false)
            return;

        fileWriteExecutor.execute(() -> {
            String levelMessage = "INFO";

            if (level == Level.WARNING)
                levelMessage = "WARN";
            else if (level == Level.SEVERE)
                levelMessage = "ERROR";

            String writeLog = "[" + new SimpleDateFormat("hh:mm:ss").format(new Date()) + " " + levelMessage + "] " + log;

            File logFile = new File(logDirectory, LocalDate.now() + ".txt");

            try {
                if (!logDirectory.exists())
                    logDirectory.mkdirs();
                if (!logFile.exists())
                    logFile.createNewFile();

                PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));

                writer.println(writeLog);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                exception("An exception occurred while writing log to a file", e);
            }
        });
    }

    private void deleteOlderLogs()
    {
        if (!isFileWriteActive())
            return;
        if (!logDirectory.exists())
            return;

        info("Searching for 7+ days old logs...");

        boolean foundOldLogs = false;

        for (File file : logDirectory.listFiles())
        {
            try {
                if (System.currentTimeMillis() - new SimpleDateFormat("yyyy-MM-dd").parse(file.getName()).getTime()  > 604800000)
                {
                    foundOldLogs = true;
                    file.delete();
                }
            } catch (ParseException e) {
                exception("An exception occurred while deleting old logs", e);
            }
        }

        if (foundOldLogs)
            info("Deleted old logs");
        else
            info("Old logs not found");
    }
}
