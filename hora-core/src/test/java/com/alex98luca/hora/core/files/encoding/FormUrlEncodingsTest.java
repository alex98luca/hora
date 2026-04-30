package com.alex98luca.hora.core.files.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodingsTest {

    @Test
    @DisplayName("Form URL encoding should encode and decode strings with UTF-8 by default")
    void shouldEncodeAndDecodeStringsWithUtf8ByDefault() {
        String data = "report name=Q1 & Q2.csv";

        String encoded = FormUrlEncodings.encode(data);

        assertEquals("report+name%3DQ1+%26+Q2.csv", encoded);
        assertEquals(data, FormUrlEncodings.decode(encoded));
    }

    @Test
    @DisplayName("Form URL encoding should support explicit charsets")
    void shouldSupportExplicitCharsets() {
        String data = "cafe";

        String encoded = FormUrlEncodings.encode(data, StandardCharsets.ISO_8859_1);

        assertEquals(data, FormUrlEncodings.decode(encoded, StandardCharsets.ISO_8859_1));
    }

    @Test
    @DisplayName("Form URL encoding should reject invalid arguments")
    void shouldRejectInvalidArguments() {
        assertThrows(NullPointerException.class, () -> FormUrlEncodings.encode(null));
        assertThrows(NullPointerException.class, () -> FormUrlEncodings.encode("", null));
        assertThrows(NullPointerException.class, () -> FormUrlEncodings.decode(null));
        assertThrows(NullPointerException.class, () -> FormUrlEncodings.decode("", null));
        assertThrows(IllegalArgumentException.class, () -> FormUrlEncodings.decode("%"));
    }
}
