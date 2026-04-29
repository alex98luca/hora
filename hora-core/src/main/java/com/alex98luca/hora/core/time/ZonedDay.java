package com.alex98luca.hora.core.time;

import static com.alex98luca.hora.core.time.TimeValidationMessages.DATE;
import static com.alex98luca.hora.core.time.TimeValidationMessages.INSTANT;
import static com.alex98luca.hora.core.time.TimeValidationMessages.PERIOD_DURATION;
import static com.alex98luca.hora.core.time.TimeValidationMessages.ZONE;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustBePositive;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustNotBeNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A calendar day interpreted in a specific time zone.
 */
public record ZonedDay(LocalDate date, ZoneId zone) {

    public ZonedDay {
        Objects.requireNonNull(date, mustNotBeNull(DATE));
        Objects.requireNonNull(zone, mustNotBeNull(ZONE));
    }

    public static ZonedDay of(LocalDate date, ZoneId zone) {
        return new ZonedDay(date, zone);
    }

    public ZonedDateTime start() {
        return date.atStartOfDay(zone);
    }

    public ZonedDateTime endExclusive() {
        return date.plusDays(1).atStartOfDay(zone);
    }

    public Instant startInstant() {
        return start().toInstant();
    }

    public Instant endExclusiveInstant() {
        return endExclusive().toInstant();
    }

    public InstantRange instantRange() {
        return new InstantRange(startInstant(), endExclusiveInstant());
    }

    public Duration duration() {
        return instantRange().duration();
    }

    public boolean contains(Instant instant) {
        Objects.requireNonNull(instant, mustNotBeNull(INSTANT));
        return instantRange().contains(instant);
    }

    public boolean isTransitionDay() {
        return !isStandardDay();
    }

    public boolean isStandardDay() {
        return Duration.ofHours(24).equals(duration());
    }

    public boolean isShortDay() {
        return duration().compareTo(Duration.ofHours(24)) < 0;
    }

    public boolean isLongDay() {
        return duration().compareTo(Duration.ofHours(24)) > 0;
    }

    public long countIntervals(Duration periodDuration) {
        validatePeriodDuration(periodDuration);
        return duration().dividedBy(periodDuration);
    }

    public ZonedDateTime intervalStart(Duration periodDuration, long index) {
        validatePeriodDuration(periodDuration);

        long intervalCount = countIntervals(periodDuration);
        if (index < 0 || index >= intervalCount) {
            throw new IllegalArgumentException("index must be within the intervals of the requested day");
        }

        return startInstant()
            .plus(periodDuration.multipliedBy(index))
            .atZone(zone);
    }

    public long intervalIndex(Instant instant, Duration periodDuration) {
        Objects.requireNonNull(instant, mustNotBeNull(INSTANT));
        validatePeriodDuration(periodDuration);

        InstantRange range = instantRange();
        if (!range.contains(instant)) {
            throw new IllegalArgumentException("instant must be within the requested local day");
        }

        return Duration.between(range.startInclusive(), instant).dividedBy(periodDuration);
    }

    public List<ZonedDateTime> intervalStarts(Duration periodDuration) {
        long intervalCount = countIntervals(periodDuration);
        List<ZonedDateTime> starts = new ArrayList<>(Math.toIntExact(intervalCount));

        for (long index = 0; index < intervalCount; index++) {
            starts.add(intervalStart(periodDuration, index));
        }

        return List.copyOf(starts);
    }

    private static void validatePeriodDuration(Duration periodDuration) {
        Objects.requireNonNull(periodDuration, mustNotBeNull(PERIOD_DURATION));

        if (periodDuration.isZero() || periodDuration.isNegative()) {
            throw new IllegalArgumentException(mustBePositive(PERIOD_DURATION));
        }
    }
}
