package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.Service;

import java.util.Set;

public record Version(int version,
                      Set<Service> services,
                      long timestamp,
                      NetworkAddress receiver,
                      NetworkAddress transmitter,
                      long nonce,
                      String userAgent,
                      int blockHeight,
                      boolean relay)
        implements Payload {
    public Version {
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null");
        }
        if (transmitter == null) {
            throw new IllegalArgumentException("Receiver cannot be null");
        }
        if (services == null) {
            services = Set.of();
        }
    }
}
