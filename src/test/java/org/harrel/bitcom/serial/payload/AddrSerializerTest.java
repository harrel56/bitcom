package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Addr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.util.List;

class AddrSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Addr[] data = new Addr[]{
                new Addr(List.of(
                        new NetworkAddress(0, 0, InetAddress.getLoopbackAddress(), 0))
                ),
                new Addr(List.of(
                        new NetworkAddress(0, 0, InetAddress.getLoopbackAddress(), 0),
                        new NetworkAddress(1321321, 1024, InetAddress.getLoopbackAddress(), 8333),
                        new NetworkAddress(123123, 1, InetAddress.getLoopbackAddress(), 11111))
                ),
                new Addr(List.of(
                        new NetworkAddress(0, 0, InetAddress.getByName("255.255.255.255"), 0),
                        new NetworkAddress(98765, 0xff, InetAddress.getByName("0.0.0.0"), 0),
                        new NetworkAddress(98765, 0xff, InetAddress.getByName("192.168.0.1"), 0),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xdf, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18333),
                        new NetworkAddress(98765, 0xff, InetAddress.getLoopbackAddress(), 18331))
                )
        };
        for (Addr addr : data) {
            new AddrSerializer().serialize(addr, out);
            Assertions.assertEquals(addr, new AddrSerializer().deserialize(in));
        }
    }
}
