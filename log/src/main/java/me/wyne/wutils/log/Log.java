package me.wyne.wutils.log;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.Contract;

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

    public Executor getFileWriteExecutor()
    {
        return fileWriteExecutor;
    }

    public File getLogDirectory()
    {
        return logDirectory;
    }

    Log(Builder builder)
    {
        this.logger = builder.logger;
        this.config = builder.config;
        this.fileWriteExecutor = builder.fileWriteExecutor;
        this.logDirectory = builder.logDirectory;
    }

    public Log(Logger logger, LogConfig logConfig)
    {
        this.logger = logger;
        this.config = logConfig;
    }

    public Log(Logger logger, LogConfig logConfig, Executor fileWriteExecutor, File logDirectory)
    {
        this(logger, logConfig);
        this.fileWriteExecutor = fileWriteExecutor;
        this.logDirectory = logDirectory;
    }

    @Contract("-> new")
    public static Builder builder() { return new Builder(); }
    @Contract("-> new")
    public Builder toBuilder()
    {
        return new Builder(this);
    }

    public static final class Builder
    {
        private Logger logger;
        private LogConfig config = new BasicLogConfig(false, false, false, false);
        private Executor fileWriteExecutor;
        private File logDirectory;

        Builder() {}

        Builder(Log logger)
        {
            this.logger = logger.logger;
            this.config = logger.config;
            this.fileWriteExecutor = logger.fileWriteExecutor;
            this.logDirectory = logger.logDirectory;
        }

        @Contract("_ -> this")
        public Builder setLogger(Logger logger)
        {
            this.logger = logger;
            return this;
        }

        @Contract("_ -> this")
        public Builder setConfig(LogConfig logConfig)
        {
            this.config = logConfig;
            return this;
        }

        @Contract("_ -> this")
        public Builder setFileWriteExecutor(Executor fileWriteExecutor)
        {
            this.fileWriteExecutor = fileWriteExecutor;
            return this;
        }

        @Contract("_ -> this")
        public Builder setLogDirectory(File logDirectory)
        {
            this.logDirectory = logDirectory;
            return this;
        }

        @Contract("-> new")
        public Log build()
        {
            return new Log(this);
        }
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
            error(ExceptionUtils.getStackTrace(exception));
            return true;
        }
        return false;
    }

    public boolean exception(Throwable exception)
    {
        if (isActive())
        {
            error(ExceptionUtils.getStackTrace(exception));
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
        if (level == Level.WARNING && config.writeWarn() == false)
            return;

        fileWriteExecutor.execute(() -> {
            String levelMessage = "INFO";

            if (level == Level.WARNING)
                levelMessage = "WARN";
            else if (level == Level.SEVERE)
                levelMessage = "ERROR";

            String writeLog = "[" + new SimpleDateFormat("hh:mm:ss").format(new Date()) + " " + levelMessage + "]: " + log;

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
                exception("An exception occurred trying to write log to a file", e);
            }
        });
    }

    public void deleteOlderLogs()
    {
        if (!isFileWriteActive())
            return;
        if (!logDirectory.exists())
            return;

        info("Searching for 7+ days old logs...");

        boolean foundOldLogs = false;

        for (File file : logDirectory.listFiles())
        {
            if (!file.getName().endsWith(".txt"))
                continue;

            try {
                if (System.currentTimeMillis() - new SimpleDateFormat("yyyy-MM-dd").parse(file.getName()).getTime() > 604800000)
                {
                    foundOldLogs = true;
                    file.delete();
                }
            } catch (ParseException e) {
                exception("An exception occurred trying to delete old logs", e);
            }
        }

        if (foundOldLogs)
            info("Deleted old logs");
        else
            info("Old logs not found");
    }
}
