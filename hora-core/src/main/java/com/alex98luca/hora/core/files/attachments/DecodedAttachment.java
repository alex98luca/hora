package com.alex98luca.hora.core.files.attachments;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeBlank;
import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.CONTENT;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.CONTENT_TYPE;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.FILENAME;

import java.util.Arrays;
import java.util.Objects;

public record DecodedAttachment(String filename, String contentType, byte[] content) {

    public DecodedAttachment {
        Objects.requireNonNull(filename, mustNotBeNull(FILENAME));
        Objects.requireNonNull(contentType, mustNotBeNull(CONTENT_TYPE));
        Objects.requireNonNull(content, mustNotBeNull(CONTENT));
        validateNotBlank(filename, FILENAME);
        validateNotBlank(contentType, CONTENT_TYPE);

        content = Arrays.copyOf(content, content.length);
    }

    public static DecodedAttachment of(String filename, byte[] content) {
        return new DecodedAttachment(
            filename,
            AttachmentContentTypes.APPLICATION_OCTET_STREAM,
            content
        );
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof DecodedAttachment(String otherFilename, String otherContentType, byte[] otherContent))) {
            return false;
        }

        return filename.equals(otherFilename)
            && contentType.equals(otherContentType)
            && Arrays.equals(content, otherContent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(filename, contentType);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "DecodedAttachment[filename=%s, contentType=%s, contentLength=%d]"
            .formatted(filename, contentType, content.length);
    }

    private static void validateNotBlank(String value, String parameterName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(mustNotBeBlank(parameterName));
        }
    }
}
