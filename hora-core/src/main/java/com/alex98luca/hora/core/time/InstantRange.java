package com.alex98luca.hora.core.time;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.time.TimeValidationMessages.END_EXCLUSIVE;
import static com.alex98luca.hora.core.time.TimeValidationMessages.DURATION;
import static com.alex98luca.hora.core.time.TimeValidationMessages.INSTANT;
import static com.alex98luca.hora.core.time.TimeValidationMessages.OTHER;
import static com.alex98luca.hora.core.time.TimeValidationMessages.START_INCLUSIVE;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A half-open instant range: startInclusive <= instant < endExclusive.
 */
public record InstantRange(Instant startInclusive, Instant endExclusive) {

    public InstantRange {
        Objects.requireNonNull(startInclusive, mustNotBeNull(START_INCLUSIVE));
        Objects.requireNonNull(endExclusive, mustNotBeNull(END_EXCLUSIVE));

        if (endExclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endExclusive must not be before startInclusive");
        }
    }

    public static InstantRange between(Instant startInclusive, Instant endExclusive) {
        return new InstantRange(startInclusive, endExclusive);
    }

    public static InstantRange startingAt(Instant startInclusive, Duration duration) {
        Objects.requireNonNull(startInclusive, mustNotBeNull(START_INCLUSIVE));
        Objects.requireNonNull(duration, mustNotBeNull(DURATION));

        if (duration.isNegative()) {
            throw new IllegalArgumentException("duration must not be negative");
        }

        return new InstantRange(startInclusive, startInclusive.plus(duration));
    }

    public Duration duration() {
        return Duration.between(startInclusive, endExclusive);
    }

    public boolean isEmpty() {
        return startInclusive.equals(endExclusive);
    }

    public boolean contains(Instant instant) {
        Objects.requireNonNull(instant, mustNotBeNull(INSTANT));
        return !instant.isBefore(startInclusive) && instant.isBefore(endExclusive);
    }

    public boolean contains(InstantRange other) {
        Objects.requireNonNull(other, mustNotBeNull(OTHER));
        return !other.startInclusive.isBefore(startInclusive) && !other.endExclusive.isAfter(endExclusive);
    }

    public boolean overlaps(InstantRange other) {
        Objects.requireNonNull(other, mustNotBeNull(OTHER));
        return startInclusive.isBefore(other.endExclusive) && other.startInclusive.isBefore(endExclusive);
    }

    public boolean abuts(InstantRange other) {
        Objects.requireNonNull(other, mustNotBeNull(OTHER));
        return endExclusive.equals(other.startInclusive) || other.endExclusive.equals(startInclusive);
    }

    public Optional<InstantRange> intersection(InstantRange other) {
        Objects.requireNonNull(other, mustNotBeNull(OTHER));

        if (!overlaps(other)) {
            return Optional.empty();
        }

        return Optional.of(new InstantRange(
            max(startInclusive, other.startInclusive),
            min(endExclusive, other.endExclusive)
        ));
    }

    public InstantRange span(InstantRange other) {
        Objects.requireNonNull(other, mustNotBeNull(OTHER));
        return new InstantRange(
            min(startInclusive, other.startInclusive),
            max(endExclusive, other.endExclusive)
        );
    }

    private static Instant min(Instant first, Instant second) {
        return first.isBefore(second) ? first : second;
    }

    private static Instant max(Instant first, Instant second) {
        return first.isAfter(second) ? first : second;
    }
}
