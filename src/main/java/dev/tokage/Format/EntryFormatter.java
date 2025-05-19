package dev.tokage.Format;

import dev.tokage.Model.LogEntry;

/**
 * Formats a {@link LogEntry} into a human-readable string for outputting to a log.
 *
 */
@FunctionalInterface
public interface EntryFormatter {
    String generate(LogEntry entry);
}
