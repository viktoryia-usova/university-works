package server.server.common.util.log.strategy;

import server.server.common.util.log.LogEntry;

public interface LogStrategy {
    String getName();
    void log(LogEntry entry);
    default void close() {}
}
