package org.harrel.bitcom.model;

import org.harrel.bitcom.client.Hashes;

public record TxIn(OutPoint previous, String script, int sequence) {
    public TxIn {
        if (previous == null) {
            throw new IllegalArgumentException("Previous cannot be null");
        }
        if (script == null || script.isEmpty() || script.length() % 2 == 1 || !Hashes.isHexString(script)) {
            throw new IllegalArgumentException("Script is invalid");
        }
        script = script.toLowerCase();
    }
}
