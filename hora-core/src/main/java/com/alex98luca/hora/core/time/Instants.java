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
}
