package server.server.common.util.log;

import lombok.extern.slf4j.Slf4j;
import server.server.common.util.log.strategy.LogStrategy;

import java.util.List;
import java.util.Map;

@Slf4j
public class AppLogger {

    private static volatile AppLogger INSTANCE;

    private final List<LogStrategy> strategies;
    private final LogProperties.Level globalMinLevel;

    public AppLogger(List<LogStrategy> strategies, LogProperties props) {
        this.strategies     = strategies;
        this.globalMinLevel = props.getMinLevel();
    }

    public static void init(AppLogger instance) {
        INSTANCE = instance;
    }

    public static String name(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static void trace(String msg, String logger)              { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.TRACE, msg, logger, null, null);; }
    public static void debug(String msg, String logger)              { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.DEBUG, msg, logger, null, null); }
    public static void info (String msg, String logger)              { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.INFO,  msg, logger, null, null);  }
    public static void warn (String msg, String logger)              { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.WARN,  msg, logger, null, null);  }
    public static void error(String msg, String logger)              { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.ERROR,  msg, logger, null, null); }
    public static void error(String msg, String logger, Throwable t) { if (INSTANCE != null) INSTANCE.log(LogEntry.Level.ERROR,  msg, logger, t, null); }

    public void log(LogEntry.Level level, String msg, String logger,
                    Throwable t, Map<String, String> ctx) {
        if (!isLevelEnabled(level)) return;

        LogEntry entry = LogEntry.builder()
                .level(level)
                .message(msg)
                .logger(logger)
                .throwable(t)
                .context(ctx != null ? ctx : Map.of())
                .build();

        for (LogStrategy s : strategies) {
            try {
                s.log(entry);
            } catch (Exception ex) {
                log.error("Strategy '{}' failed", s.getName(), ex);
            }
        }
    }

    public boolean isLevelEnabled(LogEntry.Level level) {
        return level.ordinal() >= LogEntry.Level.valueOf(globalMinLevel.name()).ordinal();
    }

    public void close() {
        strategies.forEach(LogStrategy::close);
    }
}
