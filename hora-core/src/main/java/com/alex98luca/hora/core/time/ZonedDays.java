package com.alex98luca.hora.core.time;

import static com.alex98luca.hora.core.time.TimeValidationMessages.DATE;
import static com.alex98luca.hora.core.time.TimeValidationMessages.INSTANT;
import static com.alex98luca.hora.core.time.TimeValidationMessages.PERIOD_DURATION;
import static com.alex98luca.hora.core.time.TimeValidationMessages.ZONE;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustBePositive;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustNotBeNull;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
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
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
        Objects.requireNonNull(periodDuration, mustNotBeNull(PERIOD_DURATION));

        if (periodDuration.isZero() || periodDuration.isNegative()) {
            throw new IllegalArgumentException(mustBePositive(PERIOD_DURATION));
        }

        return durationOfDay(date, zone).dividedBy(periodDuration);
    }

    /**
     * Checks if the given date contains a DST transition for the specified zone.
     */
    public static boolean isTransitionDay(LocalDate date, ZoneId zone) {
        return !isStandardDay(date, zone);
    }

    /**
     * Checks if the given date is exactly 24 hours long in the specified zone.
     */
    public static boolean isStandardDay(LocalDate date, ZoneId zone) {
        return Duration.ofHours(24).equals(durationOfDay(date, zone));
    }

    /**
     * Checks if the given date is shorter than 24 hours in the specified zone.
     */
    public static boolean isShortDay(LocalDate date, ZoneId zone) {
        return durationOfDay(date, zone).compareTo(Duration.ofHours(24)) < 0;
    }

    /**
     * Checks if the given date is longer than 24 hours in the specified zone.
     */
    public static boolean isLongDay(LocalDate date, ZoneId zone) {
        return durationOfDay(date, zone).compareTo(Duration.ofHours(24)) > 0;
    }

    /**
     * Safely gets the start of the day. In rare cases where midnight 
     * doesn't exist (DST skip), it returns the first valid time of that day.
     */
    public static ZonedDateTime getSafeStartOfDay(LocalDate date, ZoneId zone) {
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
        return date.atStartOfDay(zone);
    }

    /**
     * Safely gets the start of the next day in the specified zone.
     */
    public static ZonedDateTime getSafeStartOfNextDay(LocalDate date, ZoneId zone) {
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
        return date.plusDays(1).atStartOfDay(zone);
    }

    /**
     * Returns the actual duration of a local day in the provided zone.
     * On DST transition days this can be 23h or 25h instead of 24h.
     */
    public static Duration durationOfDay(LocalDate date, ZoneId zone) {
        return Instants.durationOfDay(date, zone);
    }

    /**
     * Checks whether an instant falls inside the given local day.
     */
    public static boolean contains(LocalDate date, ZoneId zone, Instant instant) {
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
        Objects.requireNonNull(instant, mustNotBeNull(INSTANT));

        return Instants.isWithin(
            instant,
            Instants.startOfDay(date, zone),
            Instants.startOfNextDay(date, zone)
        );
    }

    /**
     * Returns the start of a zero-based interval inside the local day.
     */
    public static ZonedDateTime intervalStart(LocalDate date, ZoneId zone, Duration periodDuration, long index) {
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
        Objects.requireNonNull(periodDuration, mustNotBeNull(PERIOD_DURATION));

        if (periodDuration.isZero() || periodDuration.isNegative()) {
            throw new IllegalArgumentException(mustBePositive(PERIOD_DURATION));
        }

        long intervalCount = countIntervalsInDay(date, zone, periodDuration);
        if (index < 0 || index >= intervalCount) {
            throw new IllegalArgumentException("index must be within the intervals of the requested day");
        }

        return Instants.startOfDay(date, zone)
            .plus(periodDuration.multipliedBy(index))
            .atZone(zone);
    }

    /**
     * Returns all full interval starts inside the local day.
     */
    public static List<ZonedDateTime> intervalStarts(LocalDate date, ZoneId zone, Duration periodDuration) {
        long intervalCount = countIntervalsInDay(date, zone, periodDuration);
        List<ZonedDateTime> starts = new ArrayList<>(Math.toIntExact(intervalCount));

        for (long index = 0; index < intervalCount; index++) {
            starts.add(intervalStart(date, zone, periodDuration, index));
        }

        return List.copyOf(starts);
    }
}
