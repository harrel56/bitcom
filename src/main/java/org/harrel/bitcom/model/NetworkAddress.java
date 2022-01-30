package org.harrel.bitcom.model;

import java.net.InetAddress;

public record NetworkAddress(int time,
                             long services,
                             InetAddress address,
                             int port) {
}
