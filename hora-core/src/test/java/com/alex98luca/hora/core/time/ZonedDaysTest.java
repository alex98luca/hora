package com.alex98luca.hora.core.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ZonedDaysTest {

    private final ZoneId rome = ZoneId.of("Europe/Rome");

    @Nested
    @DisplayName("Interval Calculation Tests")
    class IntervalCalculations {

        @Test
        @DisplayName("Should return 96 quarter-hours on a standard 24h day")
        void testStandardDayIntervals() {
            LocalDate date = LocalDate.of(2026, 6, 15);
            long intervals = ZonedDays.countIntervalsInDay(date, rome, Duration.ofMinutes(15));
            assertEquals(96, intervals);
        }

        @Test
        @DisplayName("Should return 92 quarter-hours on Spring Forward (23h day)")
        void testSpringForwardIntervals() {
            // Italy 2026 DST starts March 29
            LocalDate date = LocalDate.of(2026, 3, 29);
            long intervals = ZonedDays.countIntervalsInDay(date, rome, Duration.ofMinutes(15));
            assertEquals(92, intervals);
        }

        @Test
        @DisplayName("Should return 100 quarter-hours on Autumn Back (25h day)")
        void testAutumnBackIntervals() {
            // Italy 2026 DST ends October 25
            LocalDate date = LocalDate.of(2026, 10, 25);
            long intervals = ZonedDays.countIntervalsInDay(date, rome, Duration.ofMinutes(15));
            assertEquals(100, intervals);
        }
    }

    @Nested
    @DisplayName("Transition & Edge Case Tests")
    class EdgeCases {

        @Test
        void shouldIdentifyTransitionDaysCorrectly() {
            LocalDate springTransition = LocalDate.of(2026, 3, 29);
            LocalDate regularDay = LocalDate.of(2026, 6, 15);

            assertTrue(ZonedDays.isTransitionDay(springTransition, rome));
            assertFalse(ZonedDays.isTransitionDay(regularDay, rome));
        }

        @Test
        void shouldHandleSafeStartOfDay() {
            LocalDate date = LocalDate.of(2026, 1, 1);
            ZonedDateTime start = ZonedDays.getSafeStartOfDay(date, rome);
            
            assertEquals(0, start.getHour());
            assertEquals(0, start.getMinute());
            assertEquals(rome, start.getZone());
        }
    }

    @Nested
    @DisplayName("Validation & Defensive Tests")
    class DefensiveTests {

        @Test
        @DisplayName("Should throw exception for zero duration")
        void testZeroDurationThrows() {
            LocalDate date = LocalDate.now();
            // Pre-calculate the duration outside the lambda
            Duration zero = Duration.ZERO; 
            
            assertThrows(IllegalArgumentException.class, () -> 
                ZonedDays.countIntervalsInDay(date, rome, zero)
            );
        }

        @Test
        @DisplayName("Should throw exception for negative duration")
        void testNegativeDurationThrows() {
            LocalDate date = LocalDate.now();
            // Move the creation of the negative duration outside
            Duration negative = Duration.ofMinutes(-15); 
            
            assertThrows(IllegalArgumentException.class, () -> 
                ZonedDays.countIntervalsInDay(date, rome, negative)
            );
        }

        @Test
        @DisplayName("Should throw exception for null inputs")
        void testNullChecks() {
            LocalDate date = LocalDate.now();
            Duration hour = Duration.ofHours(1);

            // Each lambda now contains EXACTLY one method call
            assertThrows(NullPointerException.class, () -> 
                ZonedDays.getSafeStartOfDay(null, rome)
            );
            
            assertThrows(NullPointerException.class, () -> 
                ZonedDays.countIntervalsInDay(date, null, hour)
            );
        }
    }
}
