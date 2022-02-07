package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.TxIn;
import org.harrel.bitcom.model.TxOut;

import java.util.Collection;

public record Tx(int version, Collection<TxIn> inputs, Collection<TxOut> outputs, int lockTime) implements Payload {
    public Tx {
        if(inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("Tx message must contain valid number of inputs (min=1)");
        }
        if(outputs == null || outputs.isEmpty()) {
            throw new IllegalArgumentException("Tx message must contain valid number of outputs (min=1)");
        }
    }
}
