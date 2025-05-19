package dev.tokage.Format;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Defines how a log filename should be generated (e.g. static,date, timestamped)
 * used by {@link FileNameFormatter}
 */
public enum FileNameFormatPreset implements FileNameFormatter {
    STATIC(x -> String.format("%s.txt", x)),          // e.g., "log.txt"
    DATE(x -> String.format("%s-%s.txt", x, LocalDate.now())),    // e.g., "log-2025-05-14.txt"
    TIMESTAMP(x -> String.format("%s-%s.txt", x, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))));        // e.g., "log-2025-05-14_12-30.txt"

    private final FileNameFormatter strategy;

    FileNameFormatPreset(FileNameFormatter strategy) {
        this.strategy = strategy;
    }

    @Override
    public String generate(String entry) {
        return strategy.generate(entry);
    }
}
