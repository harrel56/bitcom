package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

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
                new Version(70016, 1085, 123456789,
                        new NetworkAddress(0, 1024, InetAddress.getByName("127.0.0.1"), 8181),
                        new NetworkAddress(0, 1085, InetAddress.getByName("127.0.0.2"), 5),
                        89529834728947911L,
                        "Testing\\*&^%$#",
                        0,
                        false),
                new Version(116, 5, -123,
                        new NetworkAddress(0, 0, InetAddress.getByName("127.12.21.1"), 0),
                        new NetworkAddress(0, 1085, InetAddress.getByName("2001:db8:85a3:8d3:1319:8a2e:370:7348"), 65000),
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