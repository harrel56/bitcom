package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.util.Range;

import java.util.Collection;

public record Addr(Collection<NetworkAddress> addresses) implements Payload {

    public static final Range SIZE_RANGE = new Range(1, 1000);

    public Addr {
        if (addresses == null || addresses.isEmpty() || addresses.size() > SIZE_RANGE.max()) {
            throw new IllegalArgumentException("Addr message must contain valid number of addresses " + SIZE_RANGE);
        }
    }
}
