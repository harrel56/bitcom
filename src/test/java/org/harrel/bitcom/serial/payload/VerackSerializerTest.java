package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Verack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class VerackSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() {
        new VerackSerializer().serialize(new Verack(), out);
        Assertions.assertEquals(new Verack(), new VerackSerializer().deserialize(in));
    }
}