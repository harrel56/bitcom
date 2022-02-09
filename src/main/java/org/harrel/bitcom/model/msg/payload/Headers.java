package org.harrel.bitcom.model.msg.payload;

import java.util.Collection;

public record Headers(Collection<Block> blocks) implements Payload {
    public Headers {
        if (blocks == null || blocks.isEmpty()) {
            throw new IllegalArgumentException("Blocks cannot be null or empty");
        }
        if (blocks.stream().anyMatch(b -> !b.transactions().isEmpty())) {
            throw new IllegalArgumentException("Headers cannot contain blocks with transactions");
        }
    }
}
