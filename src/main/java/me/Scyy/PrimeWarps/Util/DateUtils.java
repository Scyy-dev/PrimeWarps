package me.Scyy.PrimeWarps.Util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final ZoneId zone = ZoneId.of("UTC+10");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    public static String format(Instant instant) {
        return instant.atZone(zone).format(dateFormatter);
    }

    public static String format(Instant instant, boolean showTime) {
        if (!showTime) return format(instant);
        return instant.atZone(zone).format(dateTimeFormatter);
    }

}
