package dev.tokage.Util;

import dev.tokage.Format.EntryFormatter;
import dev.tokage.Format.EntryFormatPreset;
import dev.tokage.Format.FileNameFormatter;
import dev.tokage.Format.FileNameFormatPreset;

import java.util.logging.Level;

public class LogBuilder {
    private FileNameFormatter fileNameFormatter = FileNameFormatPreset.DATE;
    private String logBaseName = "log";
    private String logDir = "data/logs/";
    private boolean append = true;
    private Level minLogLevel = Level.INFO;
    private Level minConsoleLevel = Level.OFF;
    private String key = "DEFAULT";
    private EntryFormatter entryFormatter = EntryFormatPreset.BASIC;

    /**
     * Sets the formatter that determines how the filename is generated.
     * use {@link FileNameFormatPreset} for presets.
     *
     * @param fileNameFormatter custom formatter or {@link FileNameFormatPreset} preset.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setFileNameFormatter(FileNameFormatter fileNameFormatter) {
        this.fileNameFormatter = fileNameFormatter;
        return this;
    }

    /**
     * Sets the base on which the filename will be generated.
     *
     * @param logBaseName base string.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setLogBaseName(String logBaseName) {
        this.logBaseName = logBaseName;
        return this;
    }

    /**
     * Sets the location of your log directory.
     *
     * @param logDir location of your directory.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setLogDir(String logDir) {
        this.logDir = logDir;
        return this;
    }

    /**
     * Sets if you want to make a new file on starting your logger.
     * Set true to add to existing files of the same name.
     * set false to wipe your log and start anew.
     *
     * @param append set append mode.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setAppend(boolean append) {
        this.append = append;
        return this;
    }

    /**
     * Sets the minimum log level for file output.
     *
     * @param level the minimum level to write to the log file.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setMinLogLevel(Level level) {
        this.minLogLevel = level;
        return this;
    }

    /**
     * Sets the minimum log level for console output.
     *
     * @param level the minimum level to write to the console.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setMinConsoleLevel(Level level) {
        this.minConsoleLevel = level;
        return this;
    }

    /**
     * Sets the unique key for you logger.
     * Non-unique keys will throw an {@link IllegalStateException} in the {@link LogLifecycleManager} unless manually set to force replace.
     *
     * @param key unique identifier for your handler.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the formatter that determines how the log entries are generated.
     * use {@link EntryFormatPreset} for presets.
     *
     * @param entryFormatter custom formatter or {@link EntryFormatPreset} preset.
     * @return this builder instance for method chaining.
     */
    public LogBuilder setEntryFormatter(EntryFormatter entryFormatter) {
        this.entryFormatter = entryFormatter;
        return this;
    }

    /**
     * builds and returns an instance of LogHandler, this instance has yet to be initialized.
     *
     * @return new LogHandler with all parameters set.
     */
    public LogHandler build() {
        return new LogHandler(fileNameFormatter,logBaseName,logDir,append,minConsoleLevel,minLogLevel,key, entryFormatter);
    }

    /**
     * builds and returns an instance of LogHandler, this instance is initialized and can be used right away.
     *
     * @return new LogHandler with all parameters set and initialized.
     */
    public LogHandler buildAndInit() {
        LogHandler build = build();
        LogLifecycleManager.register(build);
        return build;
    }
}
