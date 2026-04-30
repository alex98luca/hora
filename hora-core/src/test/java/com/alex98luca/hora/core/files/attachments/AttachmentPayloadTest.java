package com.alex98luca.hora.core.files.attachments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttachmentPayloadTest {

    @Test
    @DisplayName("Attachment payload should accept empty Base64 content for empty files")
    void shouldAcceptEmptyBase64ContentForEmptyFiles() {
        AttachmentPayload payload = new AttachmentPayload("empty.txt", "text/plain", "");

        assertEquals("empty.txt", payload.filename());
        assertEquals("text/plain", payload.contentType());
        assertEquals("", payload.base64Content());
    }

    @Test
    @DisplayName("Attachment payload should avoid printing raw Base64 content")
    void shouldAvoidPrintingRawBase64Content() {
        AttachmentPayload payload = new AttachmentPayload("file.bin", "application/octet-stream", "AAE=");

        assertEquals(
            "AttachmentPayload[filename=file.bin, contentType=application/octet-stream, base64ContentLength=4]",
            payload.toString()
        );
    }

    @Test
    @DisplayName("Attachment payload should default to octet-stream content type")
    void shouldDefaultToOctetStreamContentType() {
        AttachmentPayload payload = AttachmentPayload.of("file.bin", "AAE=");

        assertEquals(AttachmentContentTypes.APPLICATION_OCTET_STREAM, payload.contentType());
    }

    @Test
    @DisplayName("Attachment payload should reject invalid metadata")
    void shouldRejectInvalidMetadata() {
        assertThrows(NullPointerException.class, () -> new AttachmentPayload(null, "text/plain", ""));
        assertThrows(NullPointerException.class, () -> new AttachmentPayload("file.txt", null, ""));
        assertThrows(NullPointerException.class, () -> new AttachmentPayload("file.txt", "text/plain", null));
        assertThrows(IllegalArgumentException.class, () -> new AttachmentPayload(" ", "text/plain", ""));
        assertThrows(IllegalArgumentException.class, () -> new AttachmentPayload("file.txt", " ", ""));
    }
}
