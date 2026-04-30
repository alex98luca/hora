package com.alex98luca.hora.core.files;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.FilesValidationMessages.CHARSET;
import static com.alex98luca.hora.core.files.FilesValidationMessages.DATA;
import static com.alex98luca.hora.core.files.FilesValidationMessages.ENCODED_DATA;
import static com.alex98luca.hora.core.files.FilesValidationMessages.LETTER_CASE;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class HexEncodings {

    private HexEncodings() {}

    public static String encode(byte[] data) {
        return encode(data, HexLetterCase.LOWERCASE);
    }

    public static String encode(byte[] data, HexLetterCase letterCase) {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(letterCase, mustNotBeNull(LETTER_CASE));

        return letterCase.format().formatHex(data);
    }

    public static byte[] decode(String encodedData) {
        Objects.requireNonNull(encodedData, mustNotBeNull(ENCODED_DATA));

        return HexLetterCase.LOWERCASE.format().parseHex(encodedData);
    }

    public static String encodeString(String data) {
        return encodeString(data, StandardCharsets.UTF_8, HexLetterCase.LOWERCASE);
    }

    public static String encodeString(String data, Charset charset, HexLetterCase letterCase) {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return encode(data.getBytes(charset), letterCase);
    }

    public static String decodeString(String encodedData) {
        return decodeString(encodedData, StandardCharsets.UTF_8);
    }

    public static String decodeString(String encodedData, Charset charset) {
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return new String(decode(encodedData), charset);
    }
}
