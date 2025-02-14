package com.example.crm_backend.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class Timer {
    public static Long now(){
        return System.currentTimeMillis();
    }

    public static Long endOfDay(Long time){
        Instant instant = Instant.ofEpochMilli(time);
        ZonedDateTime zoned_date_time = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime day = zoned_date_time.with(LocalDateTime.MAX);
        return day.toInstant().toEpochMilli();
    }

    public static Long endOfMonth(Long time){
        Instant instant = Instant.ofEpochMilli(time);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime endOfMonth = zonedDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalDateTime.MAX);
        return endOfMonth.toInstant().toEpochMilli();
    }

    public static String timeFormat(Long time, String format){
        Instant instant = Instant.ofEpochMilli(time);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

    public static String getTimeOfDay(Long timestamp) {
        LocalTime time = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        int hour = time.getHour();

        if (hour >= 5 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 14) {
            return "Noon";
        } else if (hour >= 14 && hour < 18) {
            return "Afternoon";
        } else {
            return "Evening";
        }
    }

}
