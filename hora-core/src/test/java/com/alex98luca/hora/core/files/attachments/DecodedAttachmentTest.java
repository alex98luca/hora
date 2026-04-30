package com.alex98luca.hora.core.files.attachments;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DecodedAttachmentTest {

    @Test
    @DisplayName("Decoded attachment should protect byte array content")
    void shouldProtectByteArrayContent() {
        byte[] content = {1, 2, 3};
        DecodedAttachment attachment = new DecodedAttachment("file.bin", "application/octet-stream", content);

        content[0] = 9;
        byte[] receivedContent = attachment.content();
        receivedContent[1] = 9;

        assertArrayEquals(new byte[] {1, 2, 3}, attachment.content());
    }

    @Test
    @DisplayName("Decoded attachment should compare byte array content by value")
    void shouldCompareByteArrayContentByValue() {
        DecodedAttachment first = new DecodedAttachment("file.bin", "application/octet-stream", new byte[] {1, 2});
        DecodedAttachment second = new DecodedAttachment("file.bin", "application/octet-stream", new byte[] {1, 2});
        DecodedAttachment other = new DecodedAttachment("file.bin", "application/octet-stream", new byte[] {2, 1});

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, other);
    }

    @Test
    @DisplayName("Decoded attachment should avoid printing raw content")
    void shouldAvoidPrintingRawContent() {
        DecodedAttachment attachment = new DecodedAttachment("file.bin", "application/octet-stream", new byte[] {1, 2});

        assertEquals(
            "DecodedAttachment[filename=file.bin, contentType=application/octet-stream, contentLength=2]",
            attachment.toString()
        );
    }

    @Test
    @DisplayName("Decoded attachment should default to octet-stream content type")
    void shouldDefaultToOctetStreamContentType() {
        DecodedAttachment attachment = DecodedAttachment.of("file.bin", new byte[] {1});

        assertEquals(AttachmentContentTypes.APPLICATION_OCTET_STREAM, attachment.contentType());
    }

    @Test
    @DisplayName("Decoded attachment should reject invalid metadata")
    void shouldRejectInvalidMetadata() {
        assertThrows(NullPointerException.class, () -> new DecodedAttachment(null, "text/plain", new byte[0]));
        assertThrows(NullPointerException.class, () -> new DecodedAttachment("file.txt", null, new byte[0]));
        assertThrows(NullPointerException.class, () -> new DecodedAttachment("file.txt", "text/plain", null));
        assertThrows(IllegalArgumentException.class, () -> new DecodedAttachment(" ", "text/plain", new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> new DecodedAttachment("file.txt", " ", new byte[0]));
    }
}
