package org.harrel.bitcom.model;

public record OutPoint(int index, Hash hash) {
    public OutPoint {
        if(index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        if (hash == null) {
            throw new IllegalArgumentException("Hash cannot be null");
        }
    }
}
