package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.InventoryVector;
import org.harrel.bitcom.model.msg.payload.Inv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class InvSerializer extends PayloadSerializer<Inv> {

    @Override
    public void serialize(Inv payload, OutputStream out) throws IOException {
        writeVarInt(payload.inventory().size(), out);
        for (InventoryVector vector : payload.inventory()) {
            writeInventoryVector(vector, out);
        }
    }

    @Override
    public Inv deserialize(InputStream in) throws IOException {
        long count = readVarInt(in);
        var inventory = new ArrayList<InventoryVector>((int) count);
        for (long i = 0; i < count; i++) {
            inventory.add(readInventoryVector(in));
        }
        return new Inv(List.copyOf(inventory));
    }
}
