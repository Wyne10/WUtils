package me.wyne.wutils.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class Log4jFactory {

    public static final String DEFAULT_CONSOLE_MESSAGE_PATTERN = "[%c{1}] %msg%n";
    public static final String DEFAULT_FILE_MESSAGE_PATTERN = "[%d{HH:mm:ss} %level]: [%c{1}] %msg%n";

    private static final Map<Class<?>, Logger> existingLoggers = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(Log4jFactory.class);

    public static Logger createLogger(JavaPlugin plugin, String filePattern, Level logLevel, String logDirectory, Log fallback) {
        if (existingLoggers.containsKey(plugin.getClass()))
            return existingLoggers.get(plugin.getClass());

        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            String logFileName = logDirectory + "/" + LocalDate.now() + ".log";
            String logFilePattern = logDirectory + "/%d{yyyy-MM-dd}.log";

            PatternLayout fileLayout = PatternLayout.newBuilder()
                    .withPattern(filePattern)
                    .withConfiguration(config)
                    .build();

            TriggeringPolicy triggeringPolicy = TimeBasedTriggeringPolicy.createPolicy("1", "true");

            DefaultRolloverStrategy rollingPolicy = DefaultRolloverStrategy.createStrategy("7", null, null, null, null, true, config);

            RollingFileAppender rollingFileAppender = RollingFileAppender.createAppender(
                    logFileName,
                    logFilePattern,
                    null,
                    "RollingFile-" + plugin.getClass().getName(),
                    null,
                    null,
                    null,
                    triggeringPolicy,
                    rollingPolicy,
                    fileLayout,
                    null,
                    null,
                    null,
                    null,
                    config);
            rollingFileAppender.start();
            config.addAppender(rollingFileAppender);

            LoggerConfig loggerConfig = new LoggerConfig(plugin.getLogger().getName(), logLevel.getLevel(), true);
            loggerConfig.addAppender(rollingFileAppender, logLevel.getLevel(), null);
            config.addLogger(plugin.getLogger().getName(), loggerConfig);
            context.updateLoggers();

            existingLoggers.putIfAbsent(plugin.getClass(), new Log4jLogger(LoggerFactory.getLogger(plugin.getLogger().getName())));
        } catch (NoSuchMethodError ignored) {
            existingLoggers.putIfAbsent(plugin.getClass(), new JulLogger(fallback));
        }

        return existingLoggers.get(plugin.getClass());
    }

}
