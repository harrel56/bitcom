package org.harrel.bitcom.model.msg.payload;

import java.io.OutputStream;

public record Ping(long nonce) implements Payload {

    @Override
    public PayloadType getPayloadType() {
        return PayloadType.ping;
    }
}
