package dev.tokage.Format;

/**
 * Formats a string into a filename with extension.
 *
 */
@FunctionalInterface
public interface FileNameFormatter {
    String generate(String entry);
}
