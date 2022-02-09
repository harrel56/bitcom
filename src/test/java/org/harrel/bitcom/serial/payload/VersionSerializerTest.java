package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.Service;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.util.Set;

class VersionSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Version[] data = new Version[]{
                new Version(70016, Set.of(), 123456789,
                        new NetworkAddress(0, Set.of(Service.NODE_NETWORK_LIMITED), InetAddress.getByName("127.0.0.1"), 8181),
                        new NetworkAddress(0, Set.of(Service.NODE_NETWORK_LIMITED, Service.NODE_COMPACT_FILTERS, Service.NODE_BLOOM), InetAddress.getByName("127.0.0.2"), 5),
                        89529834728947911L,
                        "Testing\\*&^%$#",
                        0,
                        false),
                new Version(116, Set.of(Service.NODE_NETWORK), -123,
                        new NetworkAddress(0, Set.of(Service.NODE_NETWORK_LIMITED), InetAddress.getByName("127.12.21.1"), 0),
                        new NetworkAddress(0, Set.of(), InetAddress.getByName("2001:db8:85a3:8d3:1319:8a2e:370:7348"), 65000),
                        0,
                        "Testing\\*&^%$#",
                        712323,
                        true)
        };

        for (Version ver : data) {
            new VersionSerializer().serialize(ver, out);
            Version read = new VersionSerializer().deserialize(in);
            Assertions.assertEquals(ver, read);
        }
    }
}