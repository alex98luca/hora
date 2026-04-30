package com.alex98luca.hora.core.files.encoding;

import java.util.HexFormat;

public enum HexLetterCase {
    LOWERCASE(HexFormat.of()),
    UPPERCASE(HexFormat.of().withUpperCase());

    private final HexFormat format;

    HexLetterCase(HexFormat format) {
        this.format = format;
    }

    HexFormat format() {
        return format;
    }
}
