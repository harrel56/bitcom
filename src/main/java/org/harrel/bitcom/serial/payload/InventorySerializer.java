package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.InventoryVector;
import org.harrel.bitcom.model.msg.payload.InventoryPayload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class InventorySerializer<T extends InventoryPayload> extends PayloadSerializer<T> {

    private final Function<Collection<InventoryVector>, T> constructor;

    public InventorySerializer(Function<Collection<InventoryVector>, T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public void serialize(T payload, OutputStream out) throws IOException {
        writeVarInt(payload.inventory().size(), out);
        for (InventoryVector vector : payload.inventory()) {
            writeInventoryVector(vector, out);
        }
    }

    @Override
    public T deserialize(InputStream in) throws IOException {
        long count = readVarInt(in);
        var inventory = new ArrayList<InventoryVector>((int) count);
        for (long i = 0; i < count; i++) {
            inventory.add(readInventoryVector(in));
        }
        return constructor.apply(List.copyOf(inventory));
    }
}
