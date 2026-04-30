package com.alex98luca.hora.core;

public final class ValidationMessages {

    private static final String MUST_BE_POSITIVE = " must be positive";
    private static final String MUST_BE_POSITIVE_OR_ZERO = " must be positive or zero";
    private static final String MUST_NOT_BE_NULL = " must not be null";

    private ValidationMessages() {}

    public static String mustBePositive(String parameterName) {
        return parameterName + MUST_BE_POSITIVE;
    }

    public static String mustBePositiveOrZero(String parameterName) {
        return parameterName + MUST_BE_POSITIVE_OR_ZERO;
    }

    public static String mustNotBeNull(String parameterName) {
        return parameterName + MUST_NOT_BE_NULL;
    }
}
