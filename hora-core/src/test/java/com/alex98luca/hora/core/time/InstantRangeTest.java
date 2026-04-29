package com.alex98luca.hora.core.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstantRangeTest {

    @Test
    @DisplayName("Range should use half-open boundaries")
    void shouldUseHalfOpenBoundaries() {
        Instant start = Instant.parse("2026-04-26T00:00:00Z");
        Instant end = Instant.parse("2026-04-27T00:00:00Z");
        InstantRange range = new InstantRange(start, end);

        assertTrue(range.contains(start));
        assertTrue(range.contains(start.plusSeconds(1)));
        assertFalse(range.contains(end));
        assertEquals(Duration.ofDays(1), range.duration());
    }

    @Test
    @DisplayName("Factory should create a range from start and duration")
    void shouldCreateRangeFromStartAndDuration() {
        Instant start = Instant.parse("2026-04-26T10:00:00Z");

        InstantRange range = InstantRange.startingAt(start, Duration.ofMinutes(15));

        assertEquals(start, range.startInclusive());
        assertEquals(start.plus(Duration.ofMinutes(15)), range.endExclusive());
    }

    @Test
    @DisplayName("Range should reject invalid bounds")
    void shouldRejectInvalidBounds() {
        Instant start = Instant.parse("2026-04-26T00:00:00Z");
        Instant end = Instant.parse("2026-04-25T00:00:00Z");
        Duration negativeDuration = Duration.ofSeconds(-1);

        assertThrows(IllegalArgumentException.class, () -> new InstantRange(start, end));
        assertThrows(IllegalArgumentException.class, () -> InstantRange.startingAt(start, negativeDuration));
    }

    @Test
    @DisplayName("Range should detect containment and overlap")
    void shouldDetectContainmentAndOverlap() {
        InstantRange day = InstantRange.between(
            Instant.parse("2026-04-26T00:00:00Z"),
            Instant.parse("2026-04-27T00:00:00Z")
        );
        InstantRange morning = InstantRange.between(
            Instant.parse("2026-04-26T08:00:00Z"),
            Instant.parse("2026-04-26T12:00:00Z")
        );
        InstantRange nextDay = InstantRange.between(
            Instant.parse("2026-04-27T00:00:00Z"),
            Instant.parse("2026-04-28T00:00:00Z")
        );

        assertTrue(day.contains(morning));
        assertTrue(day.overlaps(morning));
        assertFalse(day.overlaps(nextDay));
        assertTrue(day.abuts(nextDay));
    }

    @Test
    @DisplayName("Range should calculate intersection and span")
    void shouldCalculateIntersectionAndSpan() {
        InstantRange first = InstantRange.between(
            Instant.parse("2026-04-26T00:00:00Z"),
            Instant.parse("2026-04-26T12:00:00Z")
        );
        InstantRange second = InstantRange.between(
            Instant.parse("2026-04-26T08:00:00Z"),
            Instant.parse("2026-04-26T16:00:00Z")
        );

        Optional<InstantRange> intersection = first.intersection(second);

        assertTrue(intersection.isPresent());
        assertEquals(
            InstantRange.between(
                Instant.parse("2026-04-26T08:00:00Z"),
                Instant.parse("2026-04-26T12:00:00Z")
            ),
            intersection.get()
        );
        assertEquals(
            InstantRange.between(
                Instant.parse("2026-04-26T00:00:00Z"),
                Instant.parse("2026-04-26T16:00:00Z")
            ),
            first.span(second)
        );
    }
}
