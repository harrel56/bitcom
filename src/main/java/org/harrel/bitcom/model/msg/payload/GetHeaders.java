package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.Hash;

import java.util.Collection;

public record GetHeaders(int version, Collection<Hash> hashes, Hash stopHash) implements Payload {

    public GetHeaders {
        if(hashes == null || hashes.isEmpty() || hashes.size() > 2000) {
            throw new IllegalArgumentException("GetHeaders message must contain valid number of block hashes (min=1, max=2000)");
        }
    }

    public GetHeaders(int version, Collection<Hash> hashes) {
        this(version, hashes, Hash.empty());
    }
}
