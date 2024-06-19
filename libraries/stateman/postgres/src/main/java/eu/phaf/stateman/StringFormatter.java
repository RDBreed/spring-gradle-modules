package eu.phaf.stateman;

import org.slf4j.helpers.MessageFormatter;

public final class StringFormatter {
    public static String format(String format, Object... params) {
        return MessageFormatter.arrayFormat(format, params).getMessage();
    }
}
