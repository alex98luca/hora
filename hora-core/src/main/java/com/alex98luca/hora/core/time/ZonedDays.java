package com.alex98luca.hora.core.time;

import java.time.*;
import java.util.Objects;

public final class ZonedDays {

    private ZonedDays() {}

    /**
     * Calculates how many full periods of a specific duration fit into a day.
     * Essential for energy market intervals (e.g., 15-min, 30-min, hourly).
     *
     * @param date           The date to check
     * @param zone           The timezone (handles DST transitions)
     * @param periodDuration The duration of the interval (e.g., Duration.ofMinutes(15))
     * @return The total number of full intervals in that specific day
     * @throws IllegalArgumentException if periodDuration is zero or negative
     */
    public static long countIntervalsInDay(LocalDate date, ZoneId zone, Duration periodDuration) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(periodDuration, "periodDuration must not be null");

        if (periodDuration.isZero() || periodDuration.isNegative()) {
            throw new IllegalArgumentException("periodDuration must be positive");
        }

        ZonedDateTime start = date.atStartOfDay(zone);
        ZonedDateTime nextDay = date.plusDays(1).atStartOfDay(zone);
        
        Duration totalDayDuration = Duration.between(start, nextDay);
        
        return totalDayDuration.dividedBy(periodDuration);
    }

    /**
     * Checks if the given date contains a DST transition for the specified zone.
     */
    public static boolean isTransitionDay(LocalDate date, ZoneId zone) {
        // We reuse the logic, but compare against a standard 24h duration
        return countIntervalsInDay(date, zone, Duration.ofHours(1)) != 24;
    }

    /**
     * Safely gets the start of the day. In rare cases where midnight 
     * doesn't exist (DST skip), it returns the first valid time of that day.
     */
    public static ZonedDateTime getSafeStartOfDay(LocalDate date, ZoneId zone) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        return date.atStartOfDay(zone);
    }
}
