package server.server.common.util.log.strategy;

import lombok.extern.slf4j.Slf4j;
import server.server.common.util.log.LogEntry;
import server.server.common.util.log.LogProperties;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ConsoleLogStrategy implements LogStrategy {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                             .withZone(ZoneId.systemDefault());

    private static final String RESET  = "\u001B[0m";
    private static final String GREY   = "\u001B[90m";
    private static final String CYAN   = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED_BG = "\u001B[41m";

    private final boolean colorized;

    public ConsoleLogStrategy(LogProperties.StrategyConfig config) {
        this.colorized = config.isColorized();
    }

    @Override
    public String getName() { return "console"; }

    @Override
    public void log(LogEntry entry) {
        String line = format(entry);
        if (entry.getLevel().ordinal() >= LogEntry.Level.WARN.ordinal()) {
            System.err.println(line);
        } else {
            System.out.println(line);
        }
        if (entry.getThrowable() != null) {
            entry.getThrowable().printStackTrace(System.err);
        }
    }

    private String format(LogEntry e) {
        String time  = FMT.format(e.getTimestamp());
        String level = String.format("%-5s", e.getLevel().name());

        if (!colorized) {
            return "[%s] %s [%s] %s".formatted(time, level, e.getLogger(), e.getMessage());
        }
        return "%s[%s]%s %s%s%s %s[%s]%s %s".formatted(
                GREY, time, RESET,
                levelColor(e.getLevel()), level, RESET,
                CYAN, e.getLogger(), RESET,
                e.getMessage());
    }

    private String levelColor(LogEntry.Level l) {
        return switch (l) {
            case TRACE -> GREY;
            case DEBUG -> "\u001B[32m";
            case INFO  -> "\u001B[34m";
            case WARN  -> YELLOW;
            case ERROR -> RED_BG;
        };
    }
}
