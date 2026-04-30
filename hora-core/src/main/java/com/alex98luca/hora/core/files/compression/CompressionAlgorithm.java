package com.alex98luca.hora.core.files.compression;

/**
 * Compression formats supported by {@link Compressions}.
 */
public enum CompressionAlgorithm {
    /**
     * GZIP format, useful for stored payloads and service-to-service messages where metadata matters.
     */
    GZIP,

    /**
     * ZLIB/Deflate format, useful when a smaller wrapper than GZIP is preferred.
     */
    DEFLATE
}
