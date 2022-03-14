package org.harrel.bitcom.util;

public record Range(int min, int max) {
    public Range {
        if(min > max) {
            throw new IllegalArgumentException("Min needs to be lesser than or equal max");
        }
    }
}
