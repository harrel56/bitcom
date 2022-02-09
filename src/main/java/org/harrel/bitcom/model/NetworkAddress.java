package org.harrel.bitcom.model;

import java.net.InetAddress;
import java.util.Set;

public record NetworkAddress(int time,
                             Set<Service> services,
                             InetAddress address,
                             int port) {
    public NetworkAddress {
        if (address == null) {
            throw new IllegalArgumentException("Address was null");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number");
        }
        if (services == null) {
            services = Set.of();
        }
    }
}
