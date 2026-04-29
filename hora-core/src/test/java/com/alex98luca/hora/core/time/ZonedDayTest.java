package com.alex98luca.hora.core.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ZonedDayTest {

    private final ZoneId rome = ZoneId.of("Europe/Rome");

    @Test
    @DisplayName("Zoned day should expose DST-safe boundaries")
    void shouldExposeDstSafeBoundaries() {
        ZonedDay day = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);

        assertEquals(ZonedDateTime.of(2026, 6, 15, 0, 0, 0, 0, rome), day.start());
        assertEquals(ZonedDateTime.of(2026, 6, 16, 0, 0, 0, 0, rome), day.endExclusive());
        assertEquals("2026-06-14T22:00:00Z", day.startInstant().toString());
        assertEquals("2026-06-15T22:00:00Z", day.endExclusiveInstant().toString());
        assertEquals(Duration.ofHours(24), day.duration());
    }

    @Test
    @DisplayName("Zoned day should expose its instant range")
    void shouldExposeInstantRange() {
        ZonedDay day = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);
        InstantRange range = day.instantRange();

        assertEquals(day.startInstant(), range.startInclusive());
        assertEquals(day.endExclusiveInstant(), range.endExclusive());
    }

    @Test
    @DisplayName("Zoned day should classify transition days")
    void shouldClassifyTransitionDays() {
        ZonedDay springForward = ZonedDay.of(LocalDate.of(2026, 3, 29), rome);
        ZonedDay autumnBack = ZonedDay.of(LocalDate.of(2026, 10, 25), rome);
        ZonedDay standard = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);

        assertTrue(springForward.isTransitionDay());
        assertTrue(springForward.isShortDay());
        assertFalse(springForward.isLongDay());

        assertTrue(autumnBack.isTransitionDay());
        assertTrue(autumnBack.isLongDay());
        assertFalse(autumnBack.isShortDay());

        assertTrue(standard.isStandardDay());
    }

    @Test
    @DisplayName("Zoned day should check whether an instant is inside the local day")
    void shouldCheckContainment() {
        ZonedDay day = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);
        Instant inside = ZonedDateTime.of(2026, 6, 15, 12, 0, 0, 0, rome).toInstant();

        assertTrue(day.contains(inside));
        assertFalse(day.contains(day.endExclusiveInstant()));
    }

    @Test
    @DisplayName("Zoned day should expose interval helpers")
    void shouldExposeIntervalHelpers() {
        ZonedDay springForward = ZonedDay.of(LocalDate.of(2026, 3, 29), rome);

        assertEquals(92, springForward.countIntervals(Duration.ofMinutes(15)));
        assertEquals(ZonedDateTime.of(2026, 3, 29, 23, 0, 0, 0, rome), springForward.intervalStart(Duration.ofHours(1), 22));

        List<ZonedDateTime> starts = springForward.intervalStarts(Duration.ofHours(1));
        assertEquals(23, starts.size());
        assertEquals(springForward.start(), starts.getFirst());
    }

    @Test
    @DisplayName("Zoned day should calculate interval index for contained instants")
    void shouldCalculateIntervalIndex() {
        ZonedDay day = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);
        Instant instant = ZonedDateTime.of(2026, 6, 15, 1, 30, 0, 0, rome).toInstant();

        assertEquals(6, day.intervalIndex(instant, Duration.ofMinutes(15)));
    }

    @Test
    @DisplayName("Zoned day should reject interval index for instants outside the day")
    void shouldRejectIntervalIndexOutsideDay() {
        ZonedDay day = ZonedDay.of(LocalDate.of(2026, 6, 15), rome);
        Instant instant = ZonedDateTime.of(2026, 6, 16, 0, 0, 0, 0, rome).toInstant();
        Duration quarterHour = Duration.ofMinutes(15);

        assertThrows(
            IllegalArgumentException.class,
            () -> day.intervalIndex(instant, quarterHour)
        );
    }

    @Test
    @DisplayName("Zoned day should reject null components")
    void shouldRejectNullComponents() {
        LocalDate date = LocalDate.of(2026, 6, 15);

        assertThrows(NullPointerException.class, () -> ZonedDay.of(null, rome));
        assertThrows(NullPointerException.class, () -> ZonedDay.of(date, null));
    }
}
