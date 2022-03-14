package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.InventoryVector;
import org.harrel.bitcom.util.Range;

import java.util.Collection;

public interface InventoryPayload extends Payload {

    Range SIZE_RANGE = new Range(1, 50_000);

    Collection<InventoryVector> inventory();

    default void validate(Collection<InventoryVector> inventory) {
        if (inventory == null || inventory.isEmpty() || inventory.size() > SIZE_RANGE.max()) {
            throw new IllegalArgumentException("Message must contain valid number of inventory vectors " + SIZE_RANGE);
        }
    }
}
