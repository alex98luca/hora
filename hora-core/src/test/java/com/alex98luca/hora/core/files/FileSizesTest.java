package com.alex98luca.hora.core.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileSizesTest {

    @Test
    @DisplayName("File sizes should convert units to bytes")
    void shouldConvertUnitsToBytes() {
        assertEquals(1_500L, FileSizes.toBytes(1500, DecimalFileSizeUnit.BYTE));
        assertEquals(2_000L, FileSizes.toBytes(2, DecimalFileSizeUnit.KILOBYTE));
        assertEquals(2_048L, FileSizes.toBytes(2, BinaryFileSizeUnit.KIBIBYTE));
    }

    @Test
    @DisplayName("File sizes should convert bytes to target units")
    void shouldConvertBytesToTargetUnits() {
        assertEquals(1.5, FileSizes.fromBytes(1_500L, DecimalFileSizeUnit.KILOBYTE));
        assertEquals(1.5, FileSizes.fromBytes(1_536L, BinaryFileSizeUnit.KIBIBYTE));
    }

    @Test
    @DisplayName("File sizes should convert between arbitrary units")
    void shouldConvertBetweenUnits() {
        assertEquals(2.0, FileSizes.convert(2_000, DecimalFileSizeUnit.KILOBYTE, DecimalFileSizeUnit.MEGABYTE));
        assertEquals(2.0, FileSizes.convert(2_048, BinaryFileSizeUnit.KIBIBYTE, BinaryFileSizeUnit.MEBIBYTE));
    }

    @Test
    @DisplayName("File sizes should support exact rounded conversions")
    void shouldSupportExactRoundedConversions() {
        BigDecimal converted = FileSizes.exactConvert(
            1,
            DecimalFileSizeUnit.KILOBYTE,
            BinaryFileSizeUnit.KIBIBYTE,
            4
        );

        assertEquals(new BigDecimal("0.9766"), converted);
    }

    @Test
    @DisplayName("File sizes should format decimal units")
    void shouldFormatDecimalUnits() {
        assertEquals("0 B", FileSizes.formatDecimal(0));
        assertEquals("999 B", FileSizes.formatDecimal(999));
        assertEquals("1 KB", FileSizes.formatDecimal(1_000));
        assertEquals("1.5 KB", FileSizes.formatDecimal(1_500));
        assertEquals("1.5 MB", FileSizes.formatDecimal(1_500_000));
    }

    @Test
    @DisplayName("File sizes should format binary units")
    void shouldFormatBinaryUnits() {
        assertEquals("1023 B", FileSizes.formatBinary(1023));
        assertEquals("1 KiB", FileSizes.formatBinary(1024));
        assertEquals("1.5 KiB", FileSizes.formatBinary(1536));
        assertEquals("1.5 MiB", FileSizes.formatBinary(1_572_864));
    }

    @Test
    @DisplayName("File sizes should reject invalid arguments")
    void shouldRejectInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> FileSizes.toBytes(-1, DecimalFileSizeUnit.BYTE));
        assertThrows(IllegalArgumentException.class, () -> FileSizes.fromBytes(-1, DecimalFileSizeUnit.BYTE));
        assertThrows(IllegalArgumentException.class, () -> FileSizes.exactConvert(
            1,
            DecimalFileSizeUnit.BYTE,
            DecimalFileSizeUnit.KILOBYTE,
            -1
        ));
        assertThrows(NullPointerException.class, () -> FileSizes.toBytes(1, null));
        assertThrows(NullPointerException.class, () -> FileSizes.convert(1, null, DecimalFileSizeUnit.BYTE));
        assertThrows(NullPointerException.class, () -> FileSizes.convert(1, DecimalFileSizeUnit.BYTE, null));
    }
}
