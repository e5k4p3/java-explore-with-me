package ru.practicum.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public final class LocalDateTimeFormatter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String toString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime toLocalDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, DATE_TIME_FORMATTER);
    }
}
