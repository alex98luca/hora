package com.alex98luca.hora.core.files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexEncodingsTest {

    @Test
    @DisplayName("Hex should encode and decode byte arrays with lowercase by default")
    void shouldEncodeAndDecodeByteArraysWithLowercaseByDefault() {
        byte[] data = {(byte) 0x00, (byte) 0x0F, (byte) 0x10, (byte) 0xAB, (byte) 0xFF};

        String encoded = HexEncodings.encode(data);

        assertEquals("000f10abff", encoded);
        assertArrayEquals(data, HexEncodings.decode(encoded));
    }

    @Test
    @DisplayName("Hex should support uppercase encoding")
    void shouldSupportUppercaseEncoding() {
        byte[] data = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

        assertEquals("ABCDEF", HexEncodings.encode(data, HexLetterCase.UPPERCASE));
        assertArrayEquals(data, HexEncodings.decode("ABCDEF"));
    }

    @Test
    @DisplayName("Hex should encode and decode strings with UTF-8 by default")
    void shouldEncodeAndDecodeStringsWithUtf8ByDefault() {
        String data = "hora payload";

        String encoded = HexEncodings.encodeString(data);

        assertEquals("686f7261207061796c6f6164", encoded);
        assertEquals(data, HexEncodings.decodeString(encoded));
    }

    @Test
    @DisplayName("Hex should reject invalid arguments and invalid encoded data")
    void shouldRejectInvalidArgumentsAndInvalidEncodedData() {
        assertThrows(NullPointerException.class, () -> HexEncodings.encode(null));
        assertThrows(NullPointerException.class, () -> HexEncodings.encode(new byte[0], null));
        assertThrows(NullPointerException.class, () -> HexEncodings.decode(null));
        assertThrows(NullPointerException.class, () -> HexEncodings.encodeString("", null, HexLetterCase.LOWERCASE));
        assertThrows(IllegalArgumentException.class, () -> HexEncodings.decode("abc"));
        assertThrows(IllegalArgumentException.class, () -> HexEncodings.decode("not-hex"));
    }
}
