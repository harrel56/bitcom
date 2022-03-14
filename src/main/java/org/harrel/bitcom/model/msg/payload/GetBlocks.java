package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.util.Range;

import java.util.Collection;

public record GetBlocks(int version, Collection<Hash> hashes, Hash stopHash) implements Payload {

    public static final Range SIZE_RANGE = new Range(1, 500);

    public GetBlocks {
        if (hashes == null || hashes.isEmpty() || hashes.size() > SIZE_RANGE.max()) {
            throw new IllegalArgumentException("GetBlocks message must contain valid number of block hashes " + SIZE_RANGE);
        }
        if (stopHash == null) {
            throw new IllegalArgumentException("StopHash cannot be null");
        }
    }

    public GetBlocks(int version, Collection<Hash> hashes) {
        this(version, hashes, Hash.empty());
    }
}
