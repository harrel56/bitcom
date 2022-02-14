package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.Hash;

import java.util.Collection;

public record GetBlocks(int version, Collection<Hash> hashes, Hash stopHash) implements Payload {

    public GetBlocks {
        if (hashes == null || hashes.isEmpty() || hashes.size() > 500) {
            throw new IllegalArgumentException("GetBlocks message must contain valid number of block hashes (min=1, max=500)");
        }
        if (stopHash == null) {
            throw new IllegalArgumentException("StopHash cannot be null");
        }
    }

    public GetBlocks(int version, Collection<Hash> hashes) {
        this(version, hashes, Hash.empty());
    }
}
