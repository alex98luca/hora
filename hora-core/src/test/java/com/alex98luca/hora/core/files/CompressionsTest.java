package com.alex98luca.hora.core.files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompressionsTest {

    @Test
    @DisplayName("GZIP should round-trip byte arrays")
    void gzipShouldRoundTripByteArrays() throws IOException {
        byte[] data = repeatedPayload().getBytes(StandardCharsets.UTF_8);

        byte[] compressed = Compressions.gzip(data);
        byte[] decompressed = Compressions.gunzip(compressed);

        assertTrue(compressed.length < data.length);
        assertArrayEquals(data, decompressed);
    }

    @Test
    @DisplayName("Deflate should round-trip byte arrays")
    void deflateShouldRoundTripByteArrays() throws IOException {
        byte[] data = repeatedPayload().getBytes(StandardCharsets.UTF_8);

        byte[] compressed = Compressions.deflate(data);
        byte[] decompressed = Compressions.inflate(compressed);

        assertTrue(compressed.length < data.length);
        assertArrayEquals(data, decompressed);
    }

    @Test
    @DisplayName("String helpers should use UTF-8 GZIP by default")
    void stringHelpersShouldUseUtf8GzipByDefault() throws IOException {
        String data = repeatedPayload() + " - Buna dimineata";

        byte[] compressed = Compressions.compressString(data);

        assertEquals(data, Compressions.decompressString(compressed));
    }

    @Test
    @DisplayName("Stream helpers should round-trip without buffering all data internally")
    void streamHelpersShouldRoundTripData() throws IOException {
        byte[] data = repeatedPayload().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();

        Compressions.gzip(new ByteArrayInputStream(data), compressed);
        Compressions.gunzip(new ByteArrayInputStream(compressed.toByteArray()), decompressed);

        assertArrayEquals(data, decompressed.toByteArray());
    }

    @Test
    @DisplayName("Decompression should reject invalid compressed data")
    void decompressionShouldRejectInvalidCompressedData() {
        byte[] invalid = "not compressed".getBytes(StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> Compressions.gunzip(invalid));
        assertThrows(IOException.class, () -> Compressions.inflate(invalid));
    }

    @Test
    @DisplayName("Compression should reject null arguments")
    void compressionShouldRejectNullArguments() {
        byte[] data = "payload".getBytes(StandardCharsets.UTF_8);

        assertThrows(NullPointerException.class, () -> Compressions.compress(null, CompressionAlgorithm.GZIP));
        assertThrows(NullPointerException.class, () -> Compressions.compress(data, null));
        assertThrows(NullPointerException.class, () -> Compressions.compressString(null));
    }

    private static String repeatedPayload() {
        return "hora payload ".repeat(1_000);
    }
}
