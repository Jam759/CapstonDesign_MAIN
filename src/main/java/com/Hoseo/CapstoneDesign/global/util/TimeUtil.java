package com.Hoseo.CapstoneDesign.global.util;


import java.time.*;
import java.util.Date;


public class TimeUtil {

    public static LocalDateTime getNowSeoulLocalDateTime() {
        Instant instant = Instant.now();
        ZonedDateTime zSeoul = instant.atZone(ZoneId.of("Asia/Seoul"));
        return zSeoul.toLocalDateTime();
    }

    public static Instant getNowSeoulInstant() {
        Instant instant = Instant.now();
        ZonedDateTime zSeoul = instant.atZone(ZoneId.of("Asia/Seoul"));
        return zSeoul.toInstant();
    }


    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul") );
    }

    public static Duration toDuration(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return Duration.between(instant, Instant.now());
    }

    public static long toEpochMilli(LocalDateTime ldt) {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        if (ldt == null) throw new IllegalArgumentException("ldt and zone must not be null");
        return ldt.atZone(zone).toInstant().toEpochMilli();
    }

}
