package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.NetworkAddress;

import java.io.OutputStream;

public record Version(int version,
                      long services,
                      long timestamp,
                      NetworkAddress receiver,
                      NetworkAddress transmitter,
                      long nonce,
                      String userAgent,
                      int blockHeight,
                      boolean relay)
        implements Payload {

    @Override
    public PayloadType getPayloadType() {
        return PayloadType.version;
    }
}
