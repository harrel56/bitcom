package org.harrel.bitcom.model;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelTest {

    @Test
    void hashValid() {
        assertDoesNotThrow(Hash::empty);
        assertDoesNotThrow(() -> new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"));
        assertDoesNotThrow(() -> new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6".toUpperCase()));
    }

    @Test
    void hashInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Hash(null));
        assertThrows(IllegalArgumentException.class, () -> new Hash(""));
        assertThrows(IllegalArgumentException.class, () -> new Hash(" "));
        assertThrows(IllegalArgumentException.class, () -> new Hash("\n"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("\t"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("\\"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("12312"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6 "));
        assertThrows(IllegalArgumentException.class, () -> new Hash("68171c61b6859a0a355da63bd5b12de3e03008 aed4e4181a31e1fff418cb7df6"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7dfg"));
        assertThrows(IllegalArgumentException.class, () -> new Hash("\r8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"));
    }

    @Test
    void inventoryVectorInvalid() {
        Hash hash = Hash.empty();
        assertThrows(IllegalArgumentException.class, () -> new InventoryVector(null, hash));
        assertThrows(IllegalArgumentException.class, () -> new InventoryVector(InventoryVector.Type.MSG_TX, null));
    }

    @Test
    void networkAddressInvalid() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        assertThrows(IllegalArgumentException.class, () -> new NetworkAddress(0, 0, null, 1));
        assertThrows(IllegalArgumentException.class, () -> new NetworkAddress(0, 0, localHost, -1));
        assertThrows(IllegalArgumentException.class, () -> new NetworkAddress(0, 0, localHost, 65536));
    }

    @Test
    void outPointInvalid() {
        Hash hash = Hash.empty();
        assertThrows(IllegalArgumentException.class, () -> new OutPoint(0, null));
        assertThrows(IllegalArgumentException.class, () -> new OutPoint(-123, hash));
    }

    @Test
    void txInInvalid() {
        OutPoint outPoint = new OutPoint(123, Hash.empty());
        assertThrows(IllegalArgumentException.class, () -> new TxIn(outPoint, "", 1));
        assertThrows(IllegalArgumentException.class, () -> new TxIn(outPoint, "a", 1));
        assertThrows(IllegalArgumentException.class, () -> new TxIn(outPoint, "a3333333333333333", 1));
        assertThrows(IllegalArgumentException.class, () -> new TxIn(null, "ababab", 1));
    }

    @Test
    void txOutInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new TxOut(0, ""));
        assertThrows(IllegalArgumentException.class, () -> new TxOut(0, "xxxx"));
        assertThrows(IllegalArgumentException.class, () -> new TxOut(0, "AABBA"));
    }
}