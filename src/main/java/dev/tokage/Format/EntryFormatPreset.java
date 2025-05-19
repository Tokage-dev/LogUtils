package dev.tokage.Format;

import dev.tokage.Model.LogEntry;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats a {@link LogEntry} into a human-readable string for outputting to a log.
 * Use with {@link dev.tokage.Util.LogBuilder#setEntryFormatter(EntryFormatter)}.
 */
public enum EntryFormatPreset implements EntryFormatter {
    BASIC(x-> String.format("[%s] %s%n",x.getLevel(),x.getMessage())),
    VERBOSE(x-> {
        Date date = new Date(x.getTime());
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return String.format("[%s][%s] %s %s%n",x.getLevel(),x.getKey(),format.format(date),x.getMessage());
    });

    private final EntryFormatter strategy;

    EntryFormatPreset(EntryFormatter strategy) {
        this.strategy = strategy;
    }

    public EntryFormatter getStrategy() {
        return strategy;
    }

    @Override
    public String generate(LogEntry entry) {
        return strategy.generate(entry);
    }
}
