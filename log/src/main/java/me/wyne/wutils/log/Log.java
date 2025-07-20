package me.wyne.wutils.log;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * JUL wrapper allowing writing logs to file and providing global singleton.
 *
 * Deprecated due to migration to slf4j/log4j based logging.
 * Used as fallback if server core doesn't support necessary log4j version.
 * </pre>
 * @see Log4jFactory
 * @see JulLogger
 */
@Deprecated
public class Log {

    public static final int DAY_DURATION_MILLISECONDS = 86400000;
    public static Log global;

    private final Logger logger;
    private final Level level;
    private Executor fileWriteExecutor;
    private File logDirectory;

    private String dateTimePattern = "HH:mm:ss";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

    private final Map<String, File> cachedFiles = new HashMap<>();
    private final Map<String, PrintWriter> cachedWriters = new HashMap<>();

    public boolean isActive()
    {
        return logger != null && level != null;
    }

    public boolean isFileWriteActive()
    {
        return fileWriteExecutor != null && logDirectory != null;
    }

    public Logger getLogger() {
        return logger;
    }

    public Level getLevel() {
        return level;
    }

    public Executor getFileWriteExecutor()
    {
        return fileWriteExecutor;
    }

    public File getLogDirectory()
    {
        return logDirectory;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setFileWriteExecutor(Executor fileWriteExecutor) {
        this.fileWriteExecutor = fileWriteExecutor;
    }

    public void setLogDirectory(File logDirectory) {
        this.logDirectory = logDirectory;
    }

    public void setDateTimePattern(String dateTimePattern) {
        this.dateTimePattern = dateTimePattern;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.systemDefault());
    }

    Log(Builder builder)
    {
        this.logger = builder.logger;
        this.level = builder.level;
        this.fileWriteExecutor = builder.fileWriteExecutor;
        this.logDirectory = builder.logDirectory;
        this.dateTimePattern = builder.dateTimePattern;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.systemDefault());
        if (isFileWriteActive() && !logDirectory.exists())
            logDirectory.mkdirs();
    }

    public Log(Logger logger, Level level)
    {
        this.logger = logger;
        this.level = level;
    }

    public Log(Logger logger, Level level, Executor fileWriteExecutor, File logDirectory)
    {
        this(logger, level);
        this.fileWriteExecutor = fileWriteExecutor;
        this.logDirectory = logDirectory;
        if (isFileWriteActive() && !logDirectory.exists())
            logDirectory.mkdirs();
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
        private Level level = Level.INFO;
        private Executor fileWriteExecutor;
        private File logDirectory;

        private String dateTimePattern = "HH:mm:ss";

        Builder() {}

        Builder(Log logger)
        {
            this.logger = logger.logger;
            this.level = logger.level;
            this.fileWriteExecutor = logger.fileWriteExecutor;
            this.logDirectory = logger.logDirectory;
            this.dateTimePattern = logger.dateTimePattern;
        }

        @Contract("_ -> this")
        public Builder setLogger(Logger logger)
        {
            this.logger = logger;
            return this;
        }

        @Contract("_ -> this")
        public Builder setLevel(Level level)
        {
            this.level = level;
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

        @Contract("_ -> this")
        public Builder setDateTimePattern(String dateTimePattern)
        {
            this.dateTimePattern = dateTimePattern;
            return this;
        }

        @Contract("-> new")
        public Log build()
        {
            return new Log(this);
        }
    }

    public boolean isLoggable(Level level) {
        return this.level.intValue() <= level.intValue();
    }

    public boolean log(Level level, String message)
    {
        if (isActive() && isLoggable(level)) {
            logger.log(level, message);
            writeLog(level, message);
            return true;
        }
        return false;
    }

    public boolean log(Level level, String message, Throwable t)
    {
        if (isActive() && isLoggable(level))
        {
            logger.log(level, message, t);
            writeLog(level, message);
            writeLog(level, t.getMessage());
            return true;
        }
        return false;
    }

    public boolean log(Level level, String message, Object arg)
    {
        if (isActive() && isLoggable(level))
        {
            ParameterizedMessage parameterizedMessage = new ParameterizedMessage(message, arg);
            logger.log(level, parameterizedMessage.getFormattedMessage());
            writeLog(level, parameterizedMessage.getFormattedMessage());
            return true;
        }
        return false;
    }

    public boolean log(Level level, String message, Object... arguments)
    {
        if (isActive() && isLoggable(level))
        {
            ParameterizedMessage parameterizedMessage = new ParameterizedMessage(message, arguments);
            logger.log(level, parameterizedMessage.getFormattedMessage());
            writeLog(level, parameterizedMessage.getFormattedMessage());
            return true;
        }
        return false;
    }

    public boolean log(Level checkLevel, Level logLevel, Level writeLevel, String message)
    {
        if (isActive() && isLoggable(checkLevel))
        {
            logger.log(logLevel, message);
            writeLog(writeLevel, message);
            return true;
        }
        return false;
    }

    public boolean log(Level checkLevel, Level logLevel, Level writeLevel, String message, Throwable t)
    {
        if (isActive() && isLoggable(checkLevel))
        {
            logger.log(logLevel, message, t);
            writeLog(writeLevel, message);
            writeLog(writeLevel, t.getMessage());
            return true;
        }
        return false;
    }

    public boolean log(Level checkLevel, Level logLevel, Level writeLevel, String message, Object arg)
    {
        if (isActive() && isLoggable(checkLevel))
        {
            ParameterizedMessage parameterizedMessage = new ParameterizedMessage(message, arg);
            logger.log(logLevel, parameterizedMessage.getFormattedMessage());
            writeLog(writeLevel, parameterizedMessage.getFormattedMessage());
            return true;
        }
        return false;
    }

    public boolean log(Level checkLevel, Level logLevel, Level writeLevel, String message, Object... arguments)
    {
        if (isActive() && isLoggable(checkLevel))
        {
            ParameterizedMessage parameterizedMessage = new ParameterizedMessage(message, arguments);
            logger.log(logLevel, parameterizedMessage.getFormattedMessage());
            writeLog(writeLevel, parameterizedMessage.getFormattedMessage());
            return true;
        }
        return false;
    }

    public void writeLog(Level level, String log)
    {
        if (!isFileWriteActive())
            return;

        fileWriteExecutor.execute(() -> {
            String levelMessage = "INFO";

            if (level == Level.WARNING)
                levelMessage = "WARN";
            else if (level == Level.SEVERE)
                levelMessage = "ERROR";
            else if (level.intValue() <= 500)
                levelMessage = "DEBUG";

            String writeLog = "[" + dateTimeFormatter.format(Instant.now()) + " " + levelMessage + "]: [" + logger.getName() + "] " + log;

            String logFileName = LocalDate.now() + ".log";
            File logFile = cachedFiles.getOrDefault(logFileName, new File(logDirectory, logFileName));
            cachedFiles.putIfAbsent(logFileName, logFile);

            try {
                if (!logFile.exists())
                    logFile.createNewFile();

                PrintWriter writer = cachedWriters.getOrDefault(logFileName, new PrintWriter(new FileWriter(logFile, true)));
                cachedWriters.putIfAbsent(logFileName, writer);

                writer.println(writeLog);
                writer.flush();
            } catch (IOException e) {
                log(Level.SEVERE, "An exception occurred trying to write log to a file", e);
            }
        });
    }

    public void writeLog(String log) {
        if (!isFileWriteActive())
            return;

        fileWriteExecutor.execute(() -> {
            String writeLog = "[" + dateTimeFormatter.format(Instant.now()) + "]: " + log;

            String logFileName = LocalDate.now() + ".txt";
            File logFile = cachedFiles.getOrDefault(logFileName, new File(logDirectory, logFileName));
            cachedFiles.putIfAbsent(logFileName, logFile);

            try {
                if (!logFile.exists())
                    logFile.createNewFile();

                PrintWriter writer = cachedWriters.getOrDefault(logFileName, new PrintWriter(new FileWriter(logFile, true)));
                cachedWriters.putIfAbsent(logFileName, writer);

                writer.println(writeLog);
                writer.flush();
            } catch (IOException e) {
                log(Level.SEVERE, "An exception occurred trying to write log to a file", e);
            }
        });
    }

    public void deleteOlderLogs() {
        deleteOlderLogs(7);
    }

    public void deleteOlderLogs(int durationDays) {
        if (!isFileWriteActive())
            return;
        if (!logDirectory.exists())
            return;

        long durationMilliseconds = durationDays * DAY_DURATION_MILLISECONDS;

        log(Level.INFO, "Searching for " + durationDays + "+ days old logs...");

        boolean foundOldLogs = false;

        for (File file : logDirectory.listFiles())
        {
            if (!file.getName().endsWith(".txt"))
                continue;

            try {
                if (System.currentTimeMillis() - new SimpleDateFormat("yyyy-MM-dd").parse(file.getName()).getTime() >= durationMilliseconds)
                {
                    foundOldLogs = true;
                    file.delete();
                }
            } catch (ParseException e) {
                log(Level.SEVERE, "An exception occurred trying to delete old logs", e);
            }
        }

        if (foundOldLogs)
            log(Level.INFO, "Deleted old logs");
        else
            log(Level.INFO, "Old logs not found");
    }
}
