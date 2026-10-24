package server.server.common.util.log;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class LogEntry {

    public enum Level { TRACE, DEBUG, INFO, WARN, ERROR }

    @Builder.Default
    private final Instant timestamp = Instant.now();

    @Builder.Default
    private final Level level = Level.INFO;

    @Builder.Default
    private final String logger = "app";

    private final String message;
    private final Throwable throwable;

    @Builder.Default
    private final Map<String, String> context = Map.of();
}
