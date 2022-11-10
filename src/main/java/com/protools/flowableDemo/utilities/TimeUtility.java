package com.protools.flowableDemo.utilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class TimeUtility {
    public static ZonedDateTime parseToZonedDateTime(String text) throws DateTimeParseException {
        if (text.length() < 19) {
            String message = String.format(
                    "Cannot deserialize value of type `java.time.ZonedDateTime` from String \"%s\"", text);

            throw new DateTimeParseException(message, text, text.length());
        }

        boolean isISODate = (text.substring(0, 10).matches("\\d{4}-\\d{2}-\\d{2}"));

        String pattern = (isISODate) ? "yyyy-MM-dd HH:mm:ss" : "dd/MM/yyyy HH:mm:ss" ;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        LocalDateTime localDateTime = LocalDateTime.parse(text.substring(0, 19), formatter);

        ZoneId zone = (text.length() > 19) ? ZoneId.of(text.substring(19)) : ZoneId.of("Europe/Paris");

        return ZonedDateTime.of(localDateTime, zone);
    }
}
