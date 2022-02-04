package me.scyphers.minecraft.primewarps.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final long DAYS_MILLI = 1000 * 60 * 60 * 24;
    public static ZoneId zone = ZoneId.systemDefault();

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    public static String format(Instant instant) {
        return instant.atZone(zone).format(dateFormatter);
    }

    public static String format(Instant instant, boolean showTime) {
        if (!showTime) return format(instant);
        return instant.atZone(zone).format(dateTimeFormatter);
    }

}
