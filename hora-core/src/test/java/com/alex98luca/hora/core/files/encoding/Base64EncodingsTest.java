package com.alex98luca.hora.core.files.encoding;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base64EncodingsTest {

    @Test
    @DisplayName("Base64 should encode and decode byte arrays with the standard variant by default")
    void shouldEncodeAndDecodeByteArraysWithStandardVariantByDefault() {
        byte[] data = "hora payload".getBytes(StandardCharsets.UTF_8);

        String encoded = Base64Encodings.encode(data);

        assertEquals("aG9yYSBwYXlsb2Fk", encoded);
        assertArrayEquals(data, Base64Encodings.decode(encoded));
    }

    @Test
    @DisplayName("Base64 should encode and decode strings with UTF-8 by default")
    void shouldEncodeAndDecodeStringsWithUtf8ByDefault() {
        String data = "Buna dimineata";

        String encoded = Base64Encodings.encodeString(data);

        assertEquals(data, Base64Encodings.decodeString(encoded));
    }

    @Test
    @DisplayName("Base64 should support URL-safe variants")
    void shouldSupportUrlSafeVariants() {
        byte[] data = {(byte) 251, (byte) 255, (byte) 255};

        assertEquals("-___", Base64Encodings.encode(data, Base64Variant.URL_SAFE));
        assertArrayEquals(data, Base64Encodings.decode("-___", Base64Variant.URL_SAFE));
        assertFalse(Base64Encodings.encode("padding".getBytes(StandardCharsets.UTF_8), Base64Variant.URL_SAFE_WITHOUT_PADDING).endsWith("="));
    }

    @Test
    @DisplayName("Base64 should support MIME payloads")
    void shouldSupportMimePayloads() {
        byte[] data = "hora payload".repeat(20).getBytes(StandardCharsets.UTF_8);

        String encoded = Base64Encodings.encode(data, Base64Variant.MIME);

        assertEquals(new String(data, StandardCharsets.UTF_8), Base64Encodings.decodeString(encoded, StandardCharsets.UTF_8, Base64Variant.MIME));
    }

    @Test
    @DisplayName("Base64 stream helpers should encode and decode payloads")
    void streamHelpersShouldEncodeAndDecodePayloads() throws IOException {
        byte[] data = "hora stream payload".repeat(100).getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();

        Base64Encodings.encode(new ByteArrayInputStream(data), encoded);
        Base64Encodings.decode(new ByteArrayInputStream(encoded.toByteArray()), decoded);

        assertArrayEquals(data, decoded.toByteArray());
    }

    @Test
    @DisplayName("Base64 stream helpers should not close caller-owned output streams")
    void streamHelpersShouldNotCloseCallerOwnedOutputStreams() throws IOException {
        byte[] data = "payload".getBytes(StandardCharsets.UTF_8);
        CloseTrackingOutputStream output = new CloseTrackingOutputStream(new ByteArrayOutputStream());

        Base64Encodings.encode(new ByteArrayInputStream(data), output);

        assertFalse(output.closed);
    }

    @Test
    @DisplayName("Base64 should reject invalid arguments and invalid encoded data")
    void shouldRejectInvalidArgumentsAndInvalidEncodedData() {
        assertThrows(NullPointerException.class, () -> Base64Encodings.encode(null));
        assertThrows(NullPointerException.class, () -> Base64Encodings.encode(new byte[0], null));
        assertThrows(NullPointerException.class, () -> Base64Encodings.decode(null));
        assertThrows(NullPointerException.class, () -> Base64Encodings.decodeString("", null, Base64Variant.STANDARD));
        assertThrows(IllegalArgumentException.class, () -> Base64Encodings.decode("not base64!"));
    }

    private static final class CloseTrackingOutputStream extends FilterOutputStream {

        private boolean closed;

        private CloseTrackingOutputStream(OutputStream output) {
            super(output);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
