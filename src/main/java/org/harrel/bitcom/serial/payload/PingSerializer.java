package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Ping;

import java.io.OutputStream;

public class PingSerializer extends PayloadSerializer<Ping> {

    @Override
    public int getExpectedByteSize() {
        return 8;
    }

    @Override
    public void serialize(Ping payload, OutputStream out) {
        // todo
    }
}
