package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.Payload;

public record Message<T extends Payload>(Header header, T payload) {

    public Message {
        if (header == null) {
            throw new IllegalArgumentException("Header cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
    }
}
