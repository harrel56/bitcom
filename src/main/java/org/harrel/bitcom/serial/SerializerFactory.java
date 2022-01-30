package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.harrel.bitcom.serial.payload.PingSerializer;
import org.harrel.bitcom.serial.payload.VersionSerializer;

public class SerializerFactory {

    public HeaderSerializer getHeaderSerializer() {
        return new HeaderSerializer();
    }

    @SuppressWarnings("unchecked")
    public <T extends Payload> PayloadSerializer<T> getPayloadSerializer(T payload) {
        return (PayloadSerializer<T>)
                switch (payload.getPayloadType()) {
                    case version -> new VersionSerializer();
                    case ping -> new PingSerializer();
                    default -> throw new UnsupportedOperationException(
                            "No serializer found for payloadType=" + payload.getPayloadType());
                };
    }
}
