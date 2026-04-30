package com.alex98luca.hora.core.files.attachments;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeBlank;
import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.BASE64_CONTENT;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.CONTENT_TYPE;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.FILENAME;

import java.util.Objects;

public record AttachmentPayload(String filename, String contentType, String base64Content) {

    public AttachmentPayload {
        Objects.requireNonNull(filename, mustNotBeNull(FILENAME));
        Objects.requireNonNull(contentType, mustNotBeNull(CONTENT_TYPE));
        Objects.requireNonNull(base64Content, mustNotBeNull(BASE64_CONTENT));
        validateNotBlank(filename, FILENAME);
        validateNotBlank(contentType, CONTENT_TYPE);
    }

    public static AttachmentPayload of(String filename, String base64Content) {
        return new AttachmentPayload(
            filename,
            AttachmentContentTypes.APPLICATION_OCTET_STREAM,
            base64Content
        );
    }

    @Override
    public String toString() {
        return "AttachmentPayload[filename=%s, contentType=%s, base64ContentLength=%d]"
            .formatted(filename, contentType, base64Content.length());
    }

    private static void validateNotBlank(String value, String parameterName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(mustNotBeBlank(parameterName));
        }
    }
}
