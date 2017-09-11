package com.dataart.edu.client.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

/**
 * Provides functional objects to be used in Client Application
 *
 * @author alitvinov
 * @see Consumer
 * @see Function
 * @see Clientapplication
 */
@Slf4j
public final class FunctionalUtil {

    private FunctionalUtil() {
    }
    /**
     * Different date time format patterns, to give user opportunity 
     * to enter date in different formats.
     */
    private final static String[] ALLOWED_DATE_TIME_FORMATS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd HH",
        "yyyy-MM-dd",
        "dd-MM-yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd-MM-yyyy HH",
        "dd-MM-yyyy"
    };

    private static DateTimeFormatter formatter;

    static {
        initDateTimeFormatter();
    }

    /**
     * Initialization of date-time formatter.
     */
    public static void initDateTimeFormatter() {
        DateTimeParser[] dateTimeParsers = new DateTimeParser[ALLOWED_DATE_TIME_FORMATS.length];
        int position = 0;
        for (String dateTimeFormat : ALLOWED_DATE_TIME_FORMATS) {
            dateTimeParsers[position++] = DateTimeFormat.forPattern(dateTimeFormat).getParser();
        }
        formatter = new DateTimeFormatterBuilder().append(null, dateTimeParsers).toFormatter();
    }

    /**
     * Check string for not empty.
     */
    public final static Predicate<String> CHECK_STRING_PREDICATE = (string) -> {
        return !string.isEmpty();
    };

    /**
     * Check string can be parsed as double;
     */
    public final static Predicate<String> CHECK_DOUBLE_PREDICATE = (string) -> {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    };

    /**
     * Check string can be parsed as date and time
     */
    public final static Predicate<String> CHECK_DATE_PREDICATE = (string) -> {
        if (!CHECK_STRING_PREDICATE.test(string)) {
            return false;
        }
        try {
            formatter.parseDateTime(string.trim());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    };

    /**
     * Consumer when string is empty.
     */
    public final static Consumer<String> STRING_ERR_CONSUMER = (string) -> {
        log.info("String is empty. Please, provide not empty string.");
    };

    /**
     * Consumer when string can not be parsed as double.
     */
    public final static Consumer<String> DOUBLE_ERR_CONSUMER = (string) -> {
        log.info("String {} can not be parsed as Number. Please, provide correct number.", string);
    };

    /**
     * Consumer when string can not be parsed as date.
     */
    public final static Consumer<String> DATE_ERR_CONSUMER = (string) -> {
        log.info("String {} can not be parsed as Date.\nPlease, provide correct date, which is in one of next formats:\n{}\n", string, Arrays.toString(ALLOWED_DATE_TIME_FORMATS));
    };

    /**
     * Get string.
     */
    public final static Function<String, String> STRING_FUNCTION = (string) -> {
        return string;
    };

    /**
     * Get double from string.
     */
    public final static Function<String, Double> DOUBLE_FUNCTION = (string) -> {
        return Double.parseDouble(string);
    };

    /**
     * Get date from string.
     */
    public final static Function<String, DateTime> DATE_FUNCTION = (string) -> {
        return formatter.parseDateTime(string.trim());
    };
}
