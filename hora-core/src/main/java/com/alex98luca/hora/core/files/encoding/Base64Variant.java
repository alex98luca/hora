package com.alex98luca.hora.core.files.encoding;

import java.util.Base64;

public enum Base64Variant {
    STANDARD(Base64.getEncoder(), Base64.getDecoder()),
    URL_SAFE(Base64.getUrlEncoder(), Base64.getUrlDecoder()),
    URL_SAFE_WITHOUT_PADDING(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder()),
    MIME(Base64.getMimeEncoder(), Base64.getMimeDecoder());

    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    Base64Variant(Base64.Encoder encoder, Base64.Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    Base64.Encoder encoder() {
        return encoder;
    }

    Base64.Decoder decoder() {
        return decoder;
    }
}
