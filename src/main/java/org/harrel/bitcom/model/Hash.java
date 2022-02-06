package org.harrel.bitcom.model;

public record Hash(String value) {
    public Hash {
        if (value == null || value.length() != 64) {
            throw new IllegalArgumentException("Hash must be 32 bytes long (64 length as String)");
        }
    }

    public static Hash empty() {
        return new Hash("0000000000000000000000000000000000000000000000000000000000000000");
    }
}
