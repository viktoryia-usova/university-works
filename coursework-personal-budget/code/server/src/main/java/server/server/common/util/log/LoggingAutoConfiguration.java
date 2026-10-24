package server.server.common.util.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.server.common.util.log.strategy.ConsoleLogStrategy;
import server.server.common.util.log.strategy.FileLogStrategy;
import server.server.common.util.log.strategy.LogStrategy;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(LogProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AppLogger.class)
    public AppLogger appLogger(LogProperties props,
                               ApplicationContext ctx,
                               @Autowired(required = false) DataSource dataSource) {
        List<LogStrategy> active = new ArrayList<>();

        for (LogProperties.StrategyConfig cfg : props.getStrategies()) {
            if (!cfg.isEnabled()) continue;

            LogStrategy strategy = switch (cfg.getType()) {
                case "console"  -> new ConsoleLogStrategy(cfg);
                case "file"     -> new FileLogStrategy(cfg);
                default -> ctx.getBean(cfg.getType(), LogStrategy.class);
            };

            active.add(strategy);
        }

        AppLogger logger = new AppLogger(active, props);
        AppLogger.init(logger);

        // Закрываем стратегии при остановке контекста
        Runtime.getRuntime().addShutdownHook(new Thread(logger::close));
        return logger;
    }
}
