package me.Scyy.PrimeWarps.Util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String format(Instant instant) {
        return instant.atZone(ZoneId.of("UTC+10")).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

}
