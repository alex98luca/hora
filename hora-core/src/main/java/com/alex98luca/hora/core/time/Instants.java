package com.alex98luca.hora.core.time;

import java.time.*;
import java.util.Objects;

public final class Instants {

    private Instants() {}

    /**
     * The "Universal Normalizer": Converts any supported temporal object 
     * into an Instant representing Midnight (00:00:00) UTC of that day.
     * * @param temporal The date/time object (LocalDate, LocalDateTime, ZonedDateTime, OffsetDateTime, Instant, or java.util.Date)
     * @return A UTC Instant at midnight.
     * @throws IllegalArgumentException if the type is unsupported.
     */
    public static Instant toUtcMidnight(Object temporal) {
        Objects.requireNonNull(temporal, "temporal object must not be null");

        LocalDate date = switch (temporal) {
            case LocalDate ld -> ld;
            case LocalDateTime ldt -> ldt.toLocalDate();
            case ZonedDateTime zdt -> zdt.toLocalDate();
            case OffsetDateTime odt -> odt.toLocalDate();
            case Instant i -> i.atZone(ZoneOffset.UTC).toLocalDate();
            case java.util.Date d -> d.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            default -> throw new IllegalArgumentException(
                "Unsupported temporal type: " + temporal.getClass().getName()
            );
        };

        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    /**
     * Returns the first valid instant of a local day in the provided zone.
     * This is DST-safe: if local midnight is skipped, java.time returns the
     * first valid time for that date.
     */
    public static Instant startOfDay(LocalDate date, ZoneId zone) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        return date.atStartOfDay(zone).toInstant();
    }

    /**
     * Returns the exclusive end instant of a local day in the provided zone.
     * Prefer this over 23:59:59.999 style end-of-day values.
     */
    public static Instant startOfNextDay(LocalDate date, ZoneId zone) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        return startOfDay(date.plusDays(1), zone);
    }

    /**
     * Returns the actual duration of a local day in the provided zone.
     * On DST transition days this can be 23h or 25h instead of 24h.
     */
    public static Duration durationOfDay(LocalDate date, ZoneId zone) {
        return Duration.between(startOfDay(date, zone), startOfNextDay(date, zone));
    }

    /**
     * Checks whether an instant is inside a half-open range:
     * startInclusive <= instant < endExclusive.
     */
    public static boolean isWithin(Instant instant, Instant startInclusive, Instant endExclusive) {
        Objects.requireNonNull(instant, "instant must not be null");
        Objects.requireNonNull(startInclusive, "startInclusive must not be null");
        Objects.requireNonNull(endExclusive, "endExclusive must not be null");

        if (endExclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endExclusive must not be before startInclusive");
        }

        return !instant.isBefore(startInclusive) && instant.isBefore(endExclusive);
    }

    /**
     * Floors an instant to the previous aligned interval boundary, using the Unix epoch as origin.
     */
    public static Instant floorToInterval(Instant instant, Duration interval) {
        Objects.requireNonNull(instant, "instant must not be null");
        validatePositive(interval, "interval");

        long intervalNanos = interval.toNanos();
        long epochNanos = Math.addExact(
            Math.multiplyExact(instant.getEpochSecond(), 1_000_000_000L),
            instant.getNano()
        );
        long flooredNanos = Math.floorDiv(epochNanos, intervalNanos) * intervalNanos;

        return Instant.ofEpochSecond(
            Math.floorDiv(flooredNanos, 1_000_000_000L),
            Math.floorMod(flooredNanos, 1_000_000_000L)
        );
    }

    /**
     * Ceils an instant to the next aligned interval boundary, using the Unix epoch as origin.
     */
    public static Instant ceilToInterval(Instant instant, Duration interval) {
        Instant floored = floorToInterval(instant, interval);
        return floored.equals(instant) ? instant : floored.plus(interval);
    }

    /**
     * Returns the zero-based interval index of an instant inside a local day.
     */
    public static long intervalIndexInDay(Instant instant, LocalDate date, ZoneId zone, Duration interval) {
        Objects.requireNonNull(instant, "instant must not be null");
        validatePositive(interval, "interval");

        Instant dayStart = startOfDay(date, zone);
        Instant dayEnd = startOfNextDay(date, zone);

        if (!isWithin(instant, dayStart, dayEnd)) {
            throw new IllegalArgumentException("instant must be within the requested local day");
        }

        return Duration.between(dayStart, instant).dividedBy(interval);
    }

    private static void validatePositive(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " must not be null");

        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }
}
