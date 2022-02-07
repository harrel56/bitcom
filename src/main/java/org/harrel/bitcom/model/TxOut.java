package org.harrel.bitcom.model;

import org.harrel.bitcom.client.Hashes;

public record TxOut(long value, String script) {
    public TxOut {
        if (script == null || script.isEmpty() || script.length() % 2 == 1 || !Hashes.isHexString(script)) {
            throw new IllegalArgumentException("Script is invalid");
        }
        script = script.toLowerCase();
    }
}
