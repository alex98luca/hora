package com.alex98luca.hora.core.files.size;

import static com.alex98luca.hora.core.files.size.FileSizeBases.DECIMAL;

public enum DecimalFileSizeUnit implements FileSizeUnit {
    BYTE(1L, "B"),
    KILOBYTE(BYTE.bytes() * DECIMAL, "KB"),
    MEGABYTE(KILOBYTE.bytes() * DECIMAL, "MB"),
    GIGABYTE(MEGABYTE.bytes() * DECIMAL, "GB"),
    TERABYTE(GIGABYTE.bytes() * DECIMAL, "TB"),
    PETABYTE(TERABYTE.bytes() * DECIMAL, "PB");

    private final long bytes;
    private final String symbol;

    DecimalFileSizeUnit(long bytes, String symbol) {
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
