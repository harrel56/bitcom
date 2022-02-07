package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.payload.*;
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
            case ADDR -> new AddrSerializer();
            case INV -> new InventorySerializer<>(Inv::new);
            case GETDATA -> new InventorySerializer<>(GetData::new);
            case NOTFOUND -> new InventorySerializer<>(NotFound::new);
            case GETBLOCKS -> new GetBlocksSerializer();
            case GETHEADERS -> new GetHeadersSerializer();
            case TX -> new GetHeadersSerializer();
            case VERSION -> new VersionSerializer();
            case VERACK -> new VerackSerializer();
            case PING -> new PingSerializer();
            case PONG -> new PongSerializer();
        };
    }
}
