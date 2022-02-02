package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Pong;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PongSerializer extends PayloadSerializer<Pong> {
    @Override
    public void serialize(Pong payload, OutputStream out) throws IOException {
        writeInt64LE(payload.nonce(), out);
    }

    @Override
    public Pong deserialize(InputStream in) throws IOException {
        return new Pong(readInt64LE(in));
    }

    @Override
    public int getExpectedByteSize() {
        return 8;
    }
}
