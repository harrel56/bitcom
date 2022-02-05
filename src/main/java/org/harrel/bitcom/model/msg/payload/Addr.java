package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.NetworkAddress;

import java.util.Collection;

public record Addr(Collection<NetworkAddress> addresses) implements Payload {

    public Addr {
        if (addresses == null || addresses.isEmpty() || addresses.size() > 1000) {
            throw new IllegalArgumentException("Addr message must contain valid number of addresses (min=1, max=1000)");
        }
    }
}
