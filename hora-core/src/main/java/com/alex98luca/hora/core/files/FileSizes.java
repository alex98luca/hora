package com.alex98luca.hora.core.files;

import static com.alex98luca.hora.core.ValidationMessages.mustBePositiveOrZero;
import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.FilesValidationMessages.AMOUNT;
import static com.alex98luca.hora.core.files.FilesValidationMessages.BYTES;
import static com.alex98luca.hora.core.files.FilesValidationMessages.FROM_UNIT;
import static com.alex98luca.hora.core.files.FilesValidationMessages.SCALE;
import static com.alex98luca.hora.core.files.FilesValidationMessages.TO_UNIT;
import static com.alex98luca.hora.core.files.FilesValidationMessages.UNIT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public final class FileSizes {

    private static final FileSizeUnit[] DECIMAL_UNITS = {
        DecimalFileSizeUnit.BYTE,
        DecimalFileSizeUnit.KILOBYTE,
        DecimalFileSizeUnit.MEGABYTE,
        DecimalFileSizeUnit.GIGABYTE,
        DecimalFileSizeUnit.TERABYTE,
        DecimalFileSizeUnit.PETABYTE
    };

    private static final FileSizeUnit[] BINARY_UNITS = {
        BinaryFileSizeUnit.BYTE,
        BinaryFileSizeUnit.KIBIBYTE,
        BinaryFileSizeUnit.MEBIBYTE,
        BinaryFileSizeUnit.GIBIBYTE,
        BinaryFileSizeUnit.TEBIBYTE,
        BinaryFileSizeUnit.PEBIBYTE
    };

    private FileSizes() {}

    public static long toBytes(long amount, FileSizeUnit unit) {
        Objects.requireNonNull(unit, mustNotBeNull(UNIT));
        validatePositiveOrZero(amount, AMOUNT);

        return Math.multiplyExact(amount, unit.bytes());
    }

    public static double fromBytes(long bytes, FileSizeUnit unit) {
        Objects.requireNonNull(unit, mustNotBeNull(UNIT));
        validatePositiveOrZero(bytes, BYTES);

        return (double) bytes / unit.bytes();
    }

    public static double convert(long amount, FileSizeUnit fromUnit, FileSizeUnit toUnit) {
        Objects.requireNonNull(fromUnit, mustNotBeNull(FROM_UNIT));
        Objects.requireNonNull(toUnit, mustNotBeNull(TO_UNIT));
        validatePositiveOrZero(amount, AMOUNT);

        return (double) Math.multiplyExact(amount, fromUnit.bytes()) / toUnit.bytes();
    }

    public static String formatDecimal(long bytes) {
        validatePositiveOrZero(bytes, BYTES);
        return format(bytes, DECIMAL_UNITS);
    }

    public static String formatBinary(long bytes) {
        validatePositiveOrZero(bytes, BYTES);
        return format(bytes, BINARY_UNITS);
    }

    public static BigDecimal exactConvert(long amount, FileSizeUnit fromUnit, FileSizeUnit toUnit, int scale) {
        Objects.requireNonNull(fromUnit, mustNotBeNull(FROM_UNIT));
        Objects.requireNonNull(toUnit, mustNotBeNull(TO_UNIT));
        validatePositiveOrZero(amount, AMOUNT);
        validatePositiveOrZero(scale, SCALE);

        return BigDecimal.valueOf(amount)
            .multiply(BigDecimal.valueOf(fromUnit.bytes()))
            .divide(BigDecimal.valueOf(toUnit.bytes()), scale, RoundingMode.HALF_UP);
    }

    private static String format(long bytes, FileSizeUnit[] units) {
        FileSizeUnit unit = selectUnit(bytes, units);
        double value = fromBytes(bytes, unit);

        return formatter().format(value) + " " + unit.symbol();
    }

    private static FileSizeUnit selectUnit(long bytes, FileSizeUnit[] units) {
        FileSizeUnit selected = units[0];

        for (FileSizeUnit unit : units) {
            if (bytes < unit.bytes()) {
                break;
            }

            selected = unit;
        }

        return selected;
    }

    private static void validatePositiveOrZero(long value, String parameterName) {
        if (value < 0) {
            throw new IllegalArgumentException(mustBePositiveOrZero(parameterName));
        }
    }

    private static DecimalFormat formatter() {
        return new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));
    }
}
