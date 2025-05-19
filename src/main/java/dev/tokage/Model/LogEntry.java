package dev.tokage.Model;


import java.util.logging.Level;

public class LogEntry {
    private final Level level;
    private final String message;
    private String key;
    private Throwable error;
    private final long time;

    public LogEntry(Level level, String message, String key, Throwable error) {
        this.level = level;
        this.message = message;
        this.key = key;
        this.error = error;
        this.time = System.currentTimeMillis();
    }

    public LogEntry(Level level, String message, String key) {
        this.level = level;
        this.message = message;
        this.key = key;
        this.time = System.currentTimeMillis();
    }

    public LogEntry(Level level, String message) {
        this.level = level;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }

    public Throwable getError() {
        return error;
    }

    public long getTime() {
        return time;
    }
}
