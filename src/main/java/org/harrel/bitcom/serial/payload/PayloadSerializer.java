package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public abstract class PayloadSerializer<T extends Payload> extends Serializer {

    public abstract void serialize(T payload, OutputStream out) throws IOException;

    public abstract int getExpectedByteSize();
}
