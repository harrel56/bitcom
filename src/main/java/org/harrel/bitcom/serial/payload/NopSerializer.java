package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Payload;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

public class NopSerializer<T extends Payload> extends PayloadSerializer<T> {

    private final Supplier<T> constructor;

    public NopSerializer(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public void serialize(T payload, OutputStream out) {
        /* Nothing to do */
    }

    @Override
    public T deserialize(InputStream in) {
        return constructor.get();
    }
}
