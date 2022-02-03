package org.harrel.bitcom.model;

import java.net.InetAddress;

public record NetworkAddress(int time,
                             long services,
                             InetAddress address,
                             int port) {
    public NetworkAddress {
        if (address == null) {
            throw new IllegalArgumentException("Address was null");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be a negative number");
        }
    }
}
