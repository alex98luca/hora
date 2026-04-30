package com.alex98luca.hora.core.files.encoding;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.CHARSET;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.DATA;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.ENCODED_DATA;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class FormUrlEncodings {

    private FormUrlEncodings() {}

    public static String encode(String data) {
        return encode(data, StandardCharsets.UTF_8);
    }

    public static String encode(String data, Charset charset) {
        Objects.requireNonNull(data, mustNotBeNull(DATA));
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return URLEncoder.encode(data, charset);
    }

    public static String decode(String encodedData) {
        return decode(encodedData, StandardCharsets.UTF_8);
    }

    public static String decode(String encodedData, Charset charset) {
        Objects.requireNonNull(encodedData, mustNotBeNull(ENCODED_DATA));
        Objects.requireNonNull(charset, mustNotBeNull(CHARSET));

        return URLDecoder.decode(encodedData, charset);
    }
}
