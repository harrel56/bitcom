package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.payload.*;

public class SerializerFactory {

    public HeaderSerializer getHeaderSerializer() {
        return new HeaderSerializer();
    }

    @SuppressWarnings("unchecked")
    public <T extends Payload> PayloadSerializer<T> getPayloadSerializer(T payload) {
        return (PayloadSerializer<T>) getPayloadSerializer(payload.getCommand());
    }

    public PayloadSerializer<?> getPayloadSerializer(Command cmd) {
        return switch (cmd) {
            case addr -> new AddrSerializer();
            case inv -> new InvSerializer();
            case version -> new VersionSerializer();
            case verack -> new VerackSerializer();
            case ping -> new PingSerializer();
            case pong -> new PongSerializer();
        };
    }
}
