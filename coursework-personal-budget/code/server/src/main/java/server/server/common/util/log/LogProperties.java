package server.server.common.util.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.logging")
public class LogProperties {

    private Level minLevel = Level.INFO;
    private List<StrategyConfig> strategies = List.of();

    public enum Level { TRACE, DEBUG, INFO, WARN, ERROR }

    @Data
    public static class StrategyConfig {
        private String type;
        private boolean enabled = true;
        private Level minLevel = null;

        // Console
        private boolean colorized = true;

        // File
        private String filePath = "logs/app.log";
        private boolean rolling = true;
        private long maxFileSizeMb = 10;
        private int maxHistory = 30;

        // Database
        private String tableName = "app_logs";
        private int batchSize = 50;
    }
}
