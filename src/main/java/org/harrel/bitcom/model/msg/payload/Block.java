package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.Hash;

import java.util.Collection;
import java.util.List;

public record Block(int version,
                    Hash previous,
                    Hash merkleRoot,
                    int timestamp,
                    int bits,
                    int nonce,
                    Collection<Tx> transactions) implements Payload {

    public Block(int version, Hash previous, Hash merkleRoot, int timestamp, int bits, int nonce) {
        this(version, previous, merkleRoot, timestamp, bits, nonce, null);
    }

    public Block {
        if (previous == null) {
            throw new IllegalArgumentException("Previous block hash cannot be null");
        }
        if (merkleRoot == null) {
            throw new IllegalArgumentException("Merkle root cannot be null");
        }
        if (transactions == null) {
            transactions = List.of(); // No transactions means it's block header
        }
    }
}
