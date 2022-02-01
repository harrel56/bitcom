package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Ping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PingSerializer extends PayloadSerializer<Ping> {

    @Override
    public int getExpectedByteSize() {
        return 8;
    }

    @Override
    public void serialize(Ping payload, OutputStream out) throws IOException {
        writeInt64LE(payload.nonce(), out);
    }

    @Override
    public Ping deserialize(InputStream in) throws IOException {
        return new Ping(readInt64LE(in.readNBytes(8)));
    }
}
