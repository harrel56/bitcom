package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Verack;

import java.io.InputStream;
import java.io.OutputStream;

public class VerackSerializer extends PayloadSerializer<Verack> {
    @Override
    public void serialize(Verack payload, OutputStream out) {
        /* Nothing to do */
    }

    @Override
    public Verack deserialize(InputStream in) {
        return new Verack();
    }
}
