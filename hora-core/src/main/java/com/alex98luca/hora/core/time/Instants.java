package com.alex98luca.hora.core.time;

import static com.alex98luca.hora.core.time.TimeValidationMessages.INSTANT;
import static com.alex98luca.hora.core.time.TimeValidationMessages.INTERVAL;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustBePositive;
import static com.alex98luca.hora.core.time.TimeValidationMessages.mustNotBeNull;

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
     * Floors an instant to the previous aligned interval boundary, using the Unix epoch as origin.
     */
    public static Instant floorToInterval(Instant instant, Duration interval) {
        Objects.requireNonNull(instant, mustNotBeNull(INSTANT));
        validateInterval(interval);

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

    private static void validateInterval(Duration duration) {
        Objects.requireNonNull(duration, mustNotBeNull(INTERVAL));

        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(mustBePositive(INTERVAL));
        }
    }
}
