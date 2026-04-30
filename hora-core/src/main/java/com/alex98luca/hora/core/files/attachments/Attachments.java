package com.alex98luca.hora.core.files.attachments;

import static com.alex98luca.hora.core.ValidationMessages.mustNotBeNull;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.ATTACHMENT;
import static com.alex98luca.hora.core.files.internal.FilesValidationMessages.ATTACHMENTS;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.alex98luca.hora.core.files.encoding.Base64Encodings;

public final class Attachments {

    private Attachments() {}

    public static AttachmentPayload encode(DecodedAttachment attachment) {
        Objects.requireNonNull(attachment, mustNotBeNull(ATTACHMENT));

        return new AttachmentPayload(
            attachment.filename(),
            attachment.contentType(),
            Base64Encodings.encode(attachment.content())
        );
    }

    public static DecodedAttachment decode(AttachmentPayload attachment) {
        Objects.requireNonNull(attachment, mustNotBeNull(ATTACHMENT));

        return new DecodedAttachment(
            attachment.filename(),
            attachment.contentType(),
            Base64Encodings.decode(attachment.base64Content())
        );
    }

    public static List<AttachmentPayload> encodeAll(Collection<DecodedAttachment> attachments) {
        Objects.requireNonNull(attachments, mustNotBeNull(ATTACHMENTS));

        return attachments.stream()
            .map(Attachments::encode)
            .toList();
    }

    public static List<DecodedAttachment> decodeAll(Collection<AttachmentPayload> attachments) {
        Objects.requireNonNull(attachments, mustNotBeNull(ATTACHMENTS));

        return attachments.stream()
            .map(Attachments::decode)
            .toList();
    }
}
