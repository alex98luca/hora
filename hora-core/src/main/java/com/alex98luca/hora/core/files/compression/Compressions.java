package com.alex98luca.hora.core.files.compression;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.ALGORITHM;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.CHARSET;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.COMPRESSED_DATA;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.DATA;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.INPUT;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.OUTPUT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public final class Compressions {

    private static final int BUFFER_SIZE = 8 * 1024;

    private Compressions() {}

    public static byte[] gzip(byte[] data) throws IOException {
        return compress(data, CompressionAlgorithm.GZIP);
    }

    public static byte[] gunzip(byte[] compressedData) throws IOException {
        return decompress(compressedData, CompressionAlgorithm.GZIP);
    }

    public static byte[] deflate(byte[] data) throws IOException {
        return compress(data, CompressionAlgorithm.DEFLATE);
    }

    public static byte[] inflate(byte[] compressedData) throws IOException {
        return decompress(compressedData, CompressionAlgorithm.DEFLATE);
    }

    public static byte[] compress(byte[] data, CompressionAlgorithm algorithm) throws IOException {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(algorithm, mustNotBeNull(ALGORITHM));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        compress(new ByteArrayInputStream(data), output, algorithm);
        return output.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData, CompressionAlgorithm algorithm) throws IOException {
        Objects.requireNonNull(compressedData, mustNotBeNull(COMPRESSED_DATA));
        Objects.requireNonNull(algorithm, mustNotBeNull(ALGORITHM));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        decompress(new ByteArrayInputStream(compressedData), output, algorithm);
        return output.toByteArray();
    }

    public static byte[] compressString(String data) throws IOException {
        return compressString(data, StandardCharsets.UTF_8, CompressionAlgorithm.GZIP);
    }

    public static byte[] compressString(String data, Charset charset, CompressionAlgorithm algorithm) throws IOException {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return compress(data.getBytes(charset), algorithm);
    }

    public static String decompressString(byte[] compressedData) throws IOException {
        return decompressString(compressedData, StandardCharsets.UTF_8, CompressionAlgorithm.GZIP);
    }

    public static String decompressString(
        byte[] compressedData,
        Charset charset,
        CompressionAlgorithm algorithm
    ) throws IOException {
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return new String(decompress(compressedData, algorithm), charset);
    }

    public static void gzip(InputStream input, OutputStream output) throws IOException {
        compress(input, output, CompressionAlgorithm.GZIP);
    }

    public static void gunzip(InputStream input, OutputStream output) throws IOException {
        decompress(input, output, CompressionAlgorithm.GZIP);
    }

    public static void compress(InputStream input, OutputStream output, CompressionAlgorithm algorithm) throws IOException {
        Objects.requireNonNull(input, mustNotBeNull(INPUT));
        Objects.requireNonNull(output, mustNotBeNull(OUTPUT));
        Objects.requireNonNull(algorithm, mustNotBeNull(ALGORITHM));

        DeflaterOutputStream compressor = switch (algorithm) {
            case GZIP -> new GZIPOutputStream(output);
            case DEFLATE -> new DeflaterOutputStream(output);
        };

        input.transferTo(compressor);
        compressor.finish();
    }

    public static void decompress(InputStream input, OutputStream output, CompressionAlgorithm algorithm) throws IOException {
        Objects.requireNonNull(input, mustNotBeNull(INPUT));
        Objects.requireNonNull(output, mustNotBeNull(OUTPUT));
        Objects.requireNonNull(algorithm, mustNotBeNull(ALGORITHM));

        InputStream decompressor = switch (algorithm) {
            case GZIP -> new GZIPInputStream(input, BUFFER_SIZE);
            case DEFLATE -> new InflaterInputStream(input);
        };

        decompressor.transferTo(output);
    }
}
