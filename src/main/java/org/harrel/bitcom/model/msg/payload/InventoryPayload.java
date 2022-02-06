package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.InventoryVector;

import java.util.Collection;

public interface InventoryPayload extends Payload {

    Collection<InventoryVector> inventory();

    default void validate(Collection<InventoryVector> inventory) {
        if (inventory == null || inventory.isEmpty() || inventory.size() > 50_000) {
            throw new IllegalArgumentException("Message must contain valid number of inventory vectors (min=1, max=50000)");
        }
    }
}
