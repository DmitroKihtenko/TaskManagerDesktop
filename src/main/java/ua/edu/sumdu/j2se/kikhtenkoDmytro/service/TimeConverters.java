package ua.edu.sumdu.j2se.kikhtenkoDmytro.service;

import org.joda.time.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimeConverters {
    public static java.time.LocalDateTime fromJoda(LocalDateTime time) {
        return java.time.LocalDateTime.of(
                time.getYear(),
                time.getMonthOfYear(),
                time.getDayOfMonth(),
                time.getHourOfDay(),
                time.getMinuteOfHour(),
                time.getSecondOfMinute(),
                time.getMillisOfSecond()
        );
    }

    public static java.time.LocalDate fromJoda(LocalDate time) {
        return java.time.LocalDate.of(time.getYear(),
                time.getMonthOfYear(),
                time.getDayOfMonth());
    }

    public static java.time.LocalTime fromJoda(LocalTime time) {
        return java.time.LocalTime.of(
                time.getHourOfDay(),
                time.getMinuteOfHour(),
                time.getSecondOfMinute(),
                time.getMillisOfSecond()
        );
    }

    public static java.time.Duration fromJoda(Period time) {
        return java.time.Duration.ofSeconds(
                time.toStandardDuration().getStandardSeconds()
        );
    }

    public static LocalDateTime toJoda(java.time.LocalDateTime date) {
        return new LocalDateTime(date.toInstant(OffsetDateTime.
                        now().getOffset()).toEpochMilli());
    }

    public static LocalDate toJoda(java.time.LocalDate date) {
        return new LocalDate(date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth());
    }

    public static LocalTime toJoda(java.time.LocalTime time) {
        return new LocalTime(
                time.getHour(),
                time.getMinute(),
                time.getSecond()
        );
    }

    public static Period toJoda(java.time.Duration time) {
        return Duration.standardSeconds(time.getSeconds()).toPeriod();
    }
}
