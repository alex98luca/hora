package com.alex98luca.hora.core.files;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.FilesValidationMessages.CHARSET;
import static com.alex98luca.hora.core.files.FilesValidationMessages.DATA;
import static com.alex98luca.hora.core.files.FilesValidationMessages.ENCODED_DATA;
import static com.alex98luca.hora.core.files.FilesValidationMessages.INPUT;
import static com.alex98luca.hora.core.files.FilesValidationMessages.OUTPUT;
import static com.alex98luca.hora.core.files.FilesValidationMessages.VARIANT;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Base64Encodings {

    private Base64Encodings() {}

    public static String encode(byte[] data) {
        return encode(data, Base64Variant.STANDARD);
    }

    public static String encode(byte[] data, Base64Variant variant) {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(variant, mustNotBeNull(VARIANT));

        return variant.encoder().encodeToString(data);
    }

    public static byte[] decode(String encodedData) {
        return decode(encodedData, Base64Variant.STANDARD);
    }

    public static byte[] decode(String encodedData, Base64Variant variant) {
        Objects.requireNonNull(encodedData, mustNotBeNull(ENCODED_DATA));
        Objects.requireNonNull(variant, mustNotBeNull(VARIANT));

        return variant.decoder().decode(encodedData);
    }

    public static String encodeString(String data) {
        return encodeString(data, StandardCharsets.UTF_8, Base64Variant.STANDARD);
    }

    public static String encodeString(String data, Charset charset, Base64Variant variant) {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return encode(data.getBytes(charset), variant);
    }

    public static String decodeString(String encodedData) {
        return decodeString(encodedData, StandardCharsets.UTF_8, Base64Variant.STANDARD);
    }

    public static String decodeString(String encodedData, Charset charset, Base64Variant variant) {
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return new String(decode(encodedData, variant), charset);
    }

    public static void encode(InputStream input, OutputStream output) throws IOException {
        encode(input, output, Base64Variant.STANDARD);
    }

    public static void encode(InputStream input, OutputStream output, Base64Variant variant) throws IOException {
        Objects.requireNonNull(input, mustNotBeNull(INPUT));
        Objects.requireNonNull(output, mustNotBeNull(OUTPUT));
        Objects.requireNonNull(variant, mustNotBeNull(VARIANT));

        try (OutputStream encoder = variant.encoder().wrap(new NonClosingOutputStream(output))) {
            input.transferTo(encoder);
        }
    }

    public static void decode(InputStream input, OutputStream output) throws IOException {
        decode(input, output, Base64Variant.STANDARD);
    }

    public static void decode(InputStream input, OutputStream output, Base64Variant variant) throws IOException {
        Objects.requireNonNull(input, mustNotBeNull(INPUT));
        Objects.requireNonNull(output, mustNotBeNull(OUTPUT));
        Objects.requireNonNull(variant, mustNotBeNull(VARIANT));

        InputStream decoder = variant.decoder().wrap(input);
        decoder.transferTo(output);
    }

    private static final class NonClosingOutputStream extends FilterOutputStream {

        private NonClosingOutputStream(OutputStream output) {
            super(output);
        }

        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            out.write(bytes, offset, length);
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}
