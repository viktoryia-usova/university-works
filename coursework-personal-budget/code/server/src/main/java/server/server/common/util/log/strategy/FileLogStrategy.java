package server.server.common.util.log.strategy;

import lombok.extern.slf4j.Slf4j;
import server.server.common.util.log.LogEntry;
import server.server.common.util.log.LogProperties;

import java.io.*;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FileLogStrategy implements LogStrategy {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                             .withZone(ZoneId.systemDefault());

    private final Path logPath;
    private final long maxBytes;
    private final boolean rolling;
    private PrintWriter writer;

    public FileLogStrategy(LogProperties.StrategyConfig config) {
        this.logPath  = Path.of(config.getFilePath());
        this.maxBytes = config.getMaxFileSizeMb() * 1024 * 1024;
        this.rolling  = config.isRolling();
        openWriter();
    }

    @Override
    public String getName() { return "file"; }

    @Override
    public synchronized void log(LogEntry entry) {
        if (rolling && needsRolling()) roll();
        writer.println(format(entry));
        if (entry.getThrowable() != null) {
            entry.getThrowable().printStackTrace(writer);
        }
        writer.flush();
    }

    @Override
    public synchronized void close() {
        if (writer != null) writer.close();
    }

    private String format(LogEntry e) {
        return "[%s] %-5s [%s] %s".formatted(
                FMT.format(e.getTimestamp()),
                e.getLevel(),
                e.getLogger(),
                e.getMessage());
    }

    private boolean needsRolling() {
        try { return Files.exists(logPath) && Files.size(logPath) >= maxBytes; }
        catch (IOException ex) { return false; }
    }

    private void roll() {
        close();
        Path rolled = logPath.resolveSibling(
                logPath.getFileName() + "." + System.currentTimeMillis());
        try { Files.move(logPath, rolled); } catch (IOException ignored) {}
        openWriter();
    }

    private void openWriter() {
        try {
            Path parent = logPath.getParent();
            if (parent != null) Files.createDirectories(parent);
            writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(logPath.toFile(), true)));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open log file: " + logPath, e);
        }
    }
}
