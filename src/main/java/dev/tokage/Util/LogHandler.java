package dev.tokage.Util;

import dev.tokage.Format.EntryFormatter;
import dev.tokage.Format.FileNameFormatter;
import dev.tokage.Format.FileNameFormatPreset;
import dev.tokage.Model.LogEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LogHandler {
    private BufferedWriter writer;
    private FileNameFormatter namingModeEnum = FileNameFormatPreset.DATE;
    private String logBaseName = "log";
    private String logDir = "data/logs/";
    private boolean append = true;
    private boolean mute = false;
    private Level minConsoleLevel = Level.OFF;
    private Level minLogLevel = Level.INFO;
    private String key;
    private int timeToFlush = 300;
    private EntryFormatter strategy;
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Queue<LogEntry> queue = new ConcurrentLinkedQueue<>();
    private boolean shutDown = false;

    volatile boolean flushScheduled = false;

    /**
     * Should only be used by the {@link LogBuilder}
     */
    LogHandler(FileNameFormatter namingModeEnum, String logBaseName, String logDir, boolean append, Level minConsoleLevel, Level minLogLevel, String key, EntryFormatter strategy) {
        this.namingModeEnum = namingModeEnum;
        this.logBaseName = logBaseName;
        this.logDir = logDir;
        this.append = append;
        this.minConsoleLevel = minConsoleLevel;
        this.minLogLevel = minLogLevel;
        this.key = key;
        this.strategy = strategy;
    }

    /**
     * Should only be used by {@link LogLifecycleManager}
     *
     * @return this instances key
     */
    String getKey() {
        return key;
    }

    /**
     * Queues a log entry for asynchronous writing to the log file.
     * <p>
     * This method schedules the log entry to be written using the internal
     * flush scheduler. The entry will not be written immediately, but will
     * be processed during the next scheduled flush cycle.
     * </p>
     * <p>
     * If the key is for a log other than this instance, passes the entry to that instance
     * </p>
     * <p>
     * If the logger has already been shut down, the entry is ignored.
     * </p>
     *
     * @param entry the log entry to write
     */
    public void log(LogEntry entry) {
        if (shutDown) return;

        if (entry.getKey() != null && !key.equalsIgnoreCase(entry.getKey())) {
            LogHandler logHandler = LogLifecycleManager.getLogHandler(entry.getKey());
            if (logHandler != null) {
                logHandler.log(entry);
            }
            return;
        }


        queue.offer(entry);
        if (!flushScheduled) {
            flushScheduled = true;
            scheduler.schedule(this::flush, timeToFlush, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Attempts to flush the queue to it's respected log file.
     * <p>
     *     entry's get passed by the {@link EntryFormatter} to be formatted into a desired string.
     * </p>
     * <p>
     *     If the entry contains a {@link Throwable} the message and stacktrace will be appended at the end
     *     of the log entry generated.
     * </p>
     *
     */
    private void flush() {
        try {
            while (!queue.isEmpty()) {
                LogEntry entry;
                if ((entry = queue.poll()) != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(strategy.generate(entry));
                    if (entry.getError() != null) {
                        builder.append(errorLog(entry.getError()));
                    }
                    if (minLogLevel.intValue() <= entry.getLevel().intValue()) {
                        writer.write(builder.toString());
                    }
                    if (minConsoleLevel.intValue() <= entry.getLevel().intValue()) {
                        System.out.print(builder);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to write log entry: " + e.getMessage());
        } finally {
            try {
                writer.flush();
            } catch (IOException e) {
                System.err.println("Failed to flush: " + e.getMessage());
            }
        }
        flushScheduled = false;
    }

    /**
     * formats the {@link Throwable} into a loggable string.
     * @param e Throwable to be formatted
     * @return Loggable string
     */

    private String errorLog(Throwable e) {
        StringWriter sw = new StringWriter();      // a fake output stream
        PrintWriter pw = new PrintWriter(sw);      // pretend we're writing to a file
        e.printStackTrace(pw);                     // print it into the fake stream
        return sw.toString();                      // tada! string version of the stacktrace
    }

    /**
     * Sets up everything needed to write to the log files.
     * Also sets a shutdown-hook on runtime to close down the logger in case of the application being closed.
     */
    void startLogger() {
        try {
            Path path = Paths.get(logDir, namingModeEnum.generate(logBaseName));
            Files.createDirectories(path.getParent());

            writer = new BufferedWriter(new FileWriter(path.toFile(), append));
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        } catch (Exception e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    /**
     * Flushes and shuts down the logger and cleans up any running threads.
     */
    public void shutdown() {
        if (shutDown) return;
        scheduler.shutdownNow();
        try {
            if (writer != null) {
                shutDown = true;
                queue.offer(new LogEntry(Level.INFO, "Logger is going to sleep. Goodbye"));
                flush();
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close logger: " + e.getMessage());
        }
    }

}
