package com.alex98luca.hora.core.files.attachments;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttachmentsTest {

    @Test
    @DisplayName("Attachments should encode and decode a single file")
    void shouldEncodeAndDecodeSingleFile() {
        DecodedAttachment decoded = new DecodedAttachment(
            "report.csv",
            "text/csv",
            "id,name\n1,hora".getBytes(StandardCharsets.UTF_8)
        );

        AttachmentPayload payload = Attachments.encode(decoded);
        DecodedAttachment roundTripped = Attachments.decode(payload);

        assertEquals("report.csv", payload.filename());
        assertEquals("text/csv", payload.contentType());
        assertEquals("aWQsbmFtZQoxLGhvcmE=", payload.base64Content());
        assertEquals(decoded.filename(), roundTripped.filename());
        assertEquals(decoded.contentType(), roundTripped.contentType());
        assertArrayEquals(decoded.content(), roundTripped.content());
    }

    @Test
    @DisplayName("Attachments should encode and decode multiple files")
    void shouldEncodeAndDecodeMultipleFiles() {
        List<DecodedAttachment> decoded = List.of(
            new DecodedAttachment("first.txt", "text/plain", "first".getBytes(StandardCharsets.UTF_8)),
            new DecodedAttachment("second.json", "application/json", "{}".getBytes(StandardCharsets.UTF_8))
        );

        List<AttachmentPayload> payloads = Attachments.encodeAll(decoded);
        List<DecodedAttachment> roundTripped = Attachments.decodeAll(payloads);

        assertEquals(2, payloads.size());
        assertEquals("Zmlyc3Q=", payloads.get(0).base64Content());
        assertEquals("e30=", payloads.get(1).base64Content());
        assertEquals(decoded.get(0).filename(), roundTripped.get(0).filename());
        assertArrayEquals(decoded.get(0).content(), roundTripped.get(0).content());
        assertEquals(decoded.get(1).filename(), roundTripped.get(1).filename());
        assertArrayEquals(decoded.get(1).content(), roundTripped.get(1).content());
    }

    @Test
    @DisplayName("Attachments should support empty files")
    void shouldSupportEmptyFiles() {
        DecodedAttachment decoded = DecodedAttachment.of("empty.bin", new byte[0]);

        AttachmentPayload payload = Attachments.encode(decoded);
        DecodedAttachment roundTripped = Attachments.decode(payload);

        assertEquals("", payload.base64Content());
        assertArrayEquals(new byte[0], roundTripped.content());
    }

    @Test
    @DisplayName("Attachments should reject invalid arguments and invalid Base64 content")
    void shouldRejectInvalidArgumentsAndInvalidBase64Content() {
        AttachmentPayload invalidPayload = new AttachmentPayload("file.txt", "text/plain", "not base64!");

        assertThrows(NullPointerException.class, () -> Attachments.encode(null));
        assertThrows(NullPointerException.class, () -> Attachments.decode(null));
        assertThrows(NullPointerException.class, () -> Attachments.encodeAll(null));
        assertThrows(NullPointerException.class, () -> Attachments.decodeAll(null));
        assertThrows(IllegalArgumentException.class, () -> Attachments.decode(invalidPayload));
    }
}
