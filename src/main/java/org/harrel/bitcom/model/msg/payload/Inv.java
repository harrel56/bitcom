package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.InventoryVector;

import java.util.Collection;

public record Inv(Collection<InventoryVector> inventory) implements Payload {

    public Inv {
        if(inventory == null || inventory.isEmpty() || inventory.size() > 50_000) {
            throw new IllegalArgumentException("Inv message must contain valid number of inventory vectors (min=1, max=50000)");
        }
    }
}
