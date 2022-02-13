package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Reject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class RejectSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Reject[] data = new Reject[]{
                new Reject(Command.VERSION, Reject.Type.MALFORMED, "ops"),
                new Reject(Command.VERACK, Reject.Type.INVALID, "ops"),
                new Reject(Command.ADDR, Reject.Type.OBSOLETE, "ops"),
                new Reject(Command.INV, Reject.Type.DUPLICATE, "ops"),
                new Reject(Command.GETDATA, Reject.Type.NONSTANDARD, "ops"),
                new Reject(Command.NOTFOUND, Reject.Type.DUST, "ops"),
                new Reject(Command.GETBLOCKS, Reject.Type.INSUFFICIENTFEE, "ops"),
                new Reject(Command.GETHEADERS, Reject.Type.CHECKPOINT, "ops"),
                new Reject(Command.TX, Reject.Type.INVALID, "ops"),
                new Reject(Command.BLOCK, Reject.Type.INVALID, "ops"),
                new Reject(Command.HEADERS, Reject.Type.INVALID, "ops"),
                new Reject(Command.GETADDR, Reject.Type.INVALID, "ops"),
                new Reject(Command.MEMPOOL, Reject.Type.INVALID, "ops"),
                new Reject(Command.PING, Reject.Type.INVALID, "opsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsopsops"),
                new Reject(Command.PONG, Reject.Type.INVALID, ""),
                new Reject(Command.GETADDR, Reject.Type.INVALID, ""),
        };

        for (Reject reject : data) {
            new RejectSerializer().serialize(reject, out);
            Reject read = new RejectSerializer().deserialize(in);
            Assertions.assertEquals(reject, read);
        }
    }
}