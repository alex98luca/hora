package com.alex98luca.hora.core.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstantsTest {

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
}
