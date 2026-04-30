package com.alex98luca.hora.core.files.size;

import static com.alex98luca.hora.core.files.size.FileSizeBases.BINARY;

public enum BinaryFileSizeUnit implements FileSizeUnit {
    BYTE(1L, "B"),
    KIBIBYTE(BYTE.bytes() * BINARY, "KiB"),
    MEBIBYTE(KIBIBYTE.bytes() * BINARY, "MiB"),
    GIBIBYTE(MEBIBYTE.bytes() * BINARY, "GiB"),
    TEBIBYTE(GIBIBYTE.bytes() * BINARY, "TiB"),
    PEBIBYTE(TEBIBYTE.bytes() * BINARY, "PiB");

    private final long bytes;
    private final String symbol;

    BinaryFileSizeUnit(long bytes, String symbol) {
        this.bytes = bytes;
        this.symbol = symbol;
    }

    @Override
    public long bytes() {
        return bytes;
    }

    @Override
    public String symbol() {
        return symbol;
    }
}
