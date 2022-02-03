package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Ping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PingSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Ping[] data = new Ping[] {
                new Ping(0xFFFFFFFFFFFFFFFFL),
                new Ping(0x0),
                new Ping(0x7FFFFFFFFFFFFFFFL),
                new Ping(0xFFFFFFFFFFFFFFF0L),
                new Ping(0xAA00AA00BB00CC00L)
        };

        for (Ping ping : data) {
            new PingSerializer().serialize(ping, out);
            Ping read = new PingSerializer().deserialize(in);
            Assertions.assertEquals(ping, read);
        }
    }
}