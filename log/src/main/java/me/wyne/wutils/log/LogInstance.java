package me.wyne.wutils.log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to log messages and save log files. This is an instance version of {@link Log}.
 * <br>Before logging messages make sure to {@link #registerLogger(Logger)} and {@link #registerConfig(LogConfig)}.
 * <br>If you want to save log files make sure to {@link #registerExecutor(Executor)} and {@link #registerLogDirectory(File)}.
 */
public final class LogInstance {

    private Logger logger = null;
    private LogConfig config = null;
    private Executor logWriteExecutor;
    private File logDirectory = null;

    /**
     * @return Are logger and config registered?
     */
    public boolean isActive()
    {
        return logger != null && config != null;
    }

    /**
     * Register {@link Logger} to log all messages.
     * @param logger Logger to register
     */
    public void registerLogger(@NotNull final Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Register {@link LogConfig} to control {@link #info(String)}, {@link #warn(String)} and {@link #error(String)} logging.
     * @param config Config to register
     */
    public void registerConfig(@NotNull final LogConfig config)
    {
        this.config = config;
    }

    /**
     * Register directory to save log files to.
     * @param directory Directory to register
     */
    public void registerLogDirectory(@NotNull final File directory)
    {
        this.logDirectory = directory;
    }

    /**
     * Register {@link Executor} that will write logs to {@link #logDirectory}.
     * @param executor Executor to register
     */
    public void registerExecutor(@NotNull final Executor executor)
    {
        this.logWriteExecutor = executor;
    }

    /**
     * @return True if logged successfully
     */
    public boolean info(@NotNull final String message)
    {
        if (isActive() && config.logInfo())
        {
            logger.info(message);
            writeLog(Level.INFO, message);
            return true;
        }
        return false;
    }

    /**
     * @return True if logged successfully
     */
    public boolean warn(@NotNull final String message)
    {
        if (isActive() && config.logWarn())
        {
            logger.warning(message);
            writeLog(Level.WARNING, message);
            return true;
        }
        return false;
    }

    /**
     * @return True if logged successfully
     */
    public boolean error(@NotNull final String message)
    {
        if (isActive() && config.logError())
        {
            logger.severe(message);
            writeLog(Level.SEVERE, message);
            return true;
        }
        return false;
    }

    /**
     * Log {@link LogMessage}.
     * @return True if logged successfully
     * @param logMessage {@link LogMessage} to log
     * @param doLog Log or don't log. Useful when using some logging config
     */
    public boolean log(@NotNull final LogMessage logMessage, final boolean doLog)
    {
        if (isActive() && doLog)
        {
            logger.log(logMessage.getLevel(), logMessage.getMessage());
            writeLog(logMessage.getLevel(), logMessage.getMessage());
            return true;
        }
        return false;
    }

    /**
     * Log {@link LogMessage} and split {@link LogMessage} by regex.
     * @return True if logged successfully
     * @param logMessage {@link LogMessage} to log
     * @param splitRegex Regex to split {@link LogMessage} by
     * @param doLog Log or don't log. Useful when using some logging config
     */
    public boolean log(@NotNull final LogMessage logMessage, @NotNull final String splitRegex, final boolean doLog)
    {
        if (isActive() && doLog)
        {
            for (String message : logMessage.getMessage().split(splitRegex))
            {
                logger.log(logMessage.getLevel(), message);
                writeLog(logMessage.getLevel(), message);
            }
            return true;
        }
        return false;
    }

    /**
     * Write log to {@link #logDirectory}.
     * @param level Log {@link Level}
     * @param log Message to log
     */
    public void writeLog(@NotNull final Level level, @NotNull final String log)
    {
        if (logDirectory == null || logWriteExecutor == null)
            return;

        logWriteExecutor.execute(() -> {
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
            } catch (Exception e) {
                error("Произошла ошибка при попытке записи лога в файл '" + LocalDate.now() + ".txt'");
                error(e.getMessage());
            }
        });
    }

}
