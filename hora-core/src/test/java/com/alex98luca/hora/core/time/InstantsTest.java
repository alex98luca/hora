package com.alex98luca.hora.core.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstantsTest {

    private final ZoneId rome = ZoneId.of("Europe/Rome");

    @Test
    @DisplayName("Universal Normalizer should handle all major temporal types")
    void testUniversalToUtcMidnight() {
        LocalDate localDate = LocalDate.of(2026, 4, 26);
        String expected = "2026-04-26T00:00:00Z";

        // 1. From LocalDate
        assertEquals(expected, Instants.toUtcMidnight(localDate).toString());

        // 2. From LocalDateTime (Should strip 14:30)
        assertEquals(expected, Instants.toUtcMidnight(localDate.atTime(14, 30)).toString());

        // 3. From ZonedDateTime (Rome Time)
        ZonedDateTime romeTime = localDate.atStartOfDay(ZoneId.of("Europe/Rome")).plusHours(10);
        assertEquals(expected, Instants.toUtcMidnight(romeTime).toString());

        // 4. From Instant
        Instant instant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().plusSeconds(500);
        assertEquals(expected, Instants.toUtcMidnight(instant).toString());

        // 5. From Legacy Date
        java.util.Date legacyDate = java.util.Date.from(instant);
        assertEquals(expected, Instants.toUtcMidnight(legacyDate).toString());
    }

    @Test
    @DisplayName("Local day bounds should respect the requested zone")
    void shouldReturnZonedDayBoundsAsInstants() {
        LocalDate date = LocalDate.of(2026, 6, 15);

        assertEquals("2026-06-14T22:00:00Z", Instants.startOfDay(date, rome).toString());
        assertEquals("2026-06-15T22:00:00Z", Instants.startOfNextDay(date, rome).toString());
        assertEquals(Duration.ofHours(24), Instants.durationOfDay(date, rome));
    }

    @Test
    @DisplayName("Local day duration should reflect DST transitions")
    void shouldReturnActualDurationOnDstDays() {
        LocalDate springForward = LocalDate.of(2026, 3, 29);
        LocalDate autumnBack = LocalDate.of(2026, 10, 25);

        assertEquals(Duration.ofHours(23), Instants.durationOfDay(springForward, rome));
        assertEquals(Duration.ofHours(25), Instants.durationOfDay(autumnBack, rome));
    }

    @Test
    @DisplayName("Range helper should use half-open boundaries")
    void shouldCheckHalfOpenRanges() {
        Instant start = Instant.parse("2026-04-26T00:00:00Z");
        Instant end = Instant.parse("2026-04-27T00:00:00Z");

        assertTrue(Instants.isWithin(start, start, end));
        assertTrue(Instants.isWithin(start.plusSeconds(1), start, end));
        assertFalse(Instants.isWithin(end, start, end));
        assertThrows(IllegalArgumentException.class, () -> Instants.isWithin(start, end, start));
    }

    @Test
    @DisplayName("Interval bucketing should floor and ceil to epoch-aligned boundaries")
    void shouldBucketInstantsToIntervals() {
        Instant instant = Instant.parse("2026-04-26T10:07:30Z");
        Duration quarterHour = Duration.ofMinutes(15);

        assertEquals("2026-04-26T10:00:00Z", Instants.floorToInterval(instant, quarterHour).toString());
        assertEquals("2026-04-26T10:15:00Z", Instants.ceilToInterval(instant, quarterHour).toString());
        assertEquals(instant, Instants.ceilToInterval(instant, Duration.ofSeconds(30)));
    }

    @Test
    @DisplayName("Interval index should be zero-based inside the local day")
    void shouldReturnIntervalIndexInsideLocalDay() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        Instant instant = ZonedDateTime.of(2026, 6, 15, 1, 30, 0, 0, rome).toInstant();

        assertEquals(6, Instants.intervalIndexInDay(instant, date, rome, Duration.ofMinutes(15)));
    }

    @Test
    @DisplayName("Interval index should reject instants outside the requested day")
    void shouldRejectIntervalIndexOutsideLocalDay() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        Instant instant = ZonedDateTime.of(2026, 6, 16, 0, 0, 0, 0, rome).toInstant();

        assertThrows(
            IllegalArgumentException.class,
            () -> Instants.intervalIndexInDay(instant, date, rome, Duration.ofMinutes(15))
        );
    }
}
