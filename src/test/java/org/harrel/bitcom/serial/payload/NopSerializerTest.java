package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.GetAddr;
import org.harrel.bitcom.model.msg.payload.MemPool;
import org.harrel.bitcom.model.msg.payload.Verack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class NopSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() {
        NopSerializer<Verack> verackSer = new NopSerializer<>(Verack::new);
        verackSer.serialize(new Verack(), out);
        Assertions.assertEquals(new Verack(), verackSer.deserialize(in));

        NopSerializer<GetAddr> getAddrSer = new NopSerializer<>(GetAddr::new);
        getAddrSer.serialize(new GetAddr(), out);
        Assertions.assertEquals(new GetAddr(), getAddrSer.deserialize(in));

        NopSerializer<MemPool> memPoolSer = new NopSerializer<>(MemPool::new);
        memPoolSer.serialize(new MemPool(), out);
        Assertions.assertEquals(new MemPool(), memPoolSer.deserialize(in));
    }
}