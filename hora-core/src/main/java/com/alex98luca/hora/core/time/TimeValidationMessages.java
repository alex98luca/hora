package com.alex98luca.hora.core.time;

final class TimeValidationMessages {

    static final String DATE = "date";
    static final String DURATION = "duration";
    static final String END_EXCLUSIVE = "endExclusive";
    static final String INSTANT = "instant";
    static final String INTERVAL = "interval";
    static final String OTHER = "other";
    static final String PERIOD_DURATION = "periodDuration";
    static final String START_INCLUSIVE = "startInclusive";
    static final String ZONE = "zone";

    private static final String MUST_BE_POSITIVE = " must be positive";
    private static final String MUST_NOT_BE_NULL = " must not be null";

    private TimeValidationMessages() {}

    static String mustBePositive(String parameterName) {
        return parameterName + MUST_BE_POSITIVE;
    }

    static String mustNotBeNull(String parameterName) {
        return parameterName + MUST_NOT_BE_NULL;
    }
}
