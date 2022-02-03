package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Pong;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class PongSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Pong[] data = new Pong[]{
                new Pong(0xFFFFFFFFFFFFFFFFL),
                new Pong(0x0),
                new Pong(0x7FFFFFFFFFFFFFFFL),
                new Pong(0xFFFFFFFFFFFFFFF0L),
                new Pong(0xAA00AA00BB00CC00L)
        };

        for (Pong pong : data) {
            new PongSerializer().serialize(pong, out);
            Pong read = new PongSerializer().deserialize(in);
            Assertions.assertEquals(pong, read);
        }
    }
}