package fr.insee.protools.backend.service.utils.log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeLogUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneId zone = ZoneId.systemDefault();
    private TimeLogUtils() {
    }

    public static String format(Instant i) {
        if (i == null) {
            return "Empty";
        }

        return i.atZone(zone).format(formatter);
    }
}
