package org.harrel.bitcom.serial;

import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.Command;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class HeaderSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        for (Command cmd : Command.values()) {
            Header header = new Header(
                    StandardConfiguration.MAIN.getMagicValue(),
                    cmd,
                    257,
                    0xABFA410F
            );
            new HeaderSerializer().serialize(header, out);

            Assertions.assertEquals(header, new HeaderSerializer().deserialize(in));
        }
    }
}