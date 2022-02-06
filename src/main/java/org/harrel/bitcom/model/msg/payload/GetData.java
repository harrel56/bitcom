package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.InventoryVector;

import java.util.Collection;

public record GetData(Collection<InventoryVector> inventory) implements InventoryPayload {

    public GetData {
        validate(inventory);
    }
}
