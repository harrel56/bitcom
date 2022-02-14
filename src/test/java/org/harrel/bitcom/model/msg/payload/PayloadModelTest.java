package org.harrel.bitcom.model.msg.payload;

import org.harrel.bitcom.model.*;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayloadModelTest {

    @Test
    void addr() {
        Collection<NetworkAddress> empty = List.of();
        Collection<NetworkAddress> full = Stream
                .generate(() -> new NetworkAddress(0, null, InetAddress.getLoopbackAddress(), 8080))
                .limit(1001)
                .toList();
        assertThrows(IllegalArgumentException.class, () -> new Addr(null));
        assertThrows(IllegalArgumentException.class, () -> new Addr(empty));
        assertThrows(IllegalArgumentException.class, () -> new Addr(full));
    }

    @Test
    void block() {
        Hash empty = Hash.empty();
        assertThrows(IllegalArgumentException.class, () -> new Block(101, null, empty, 123, 321, 123));
        assertThrows(IllegalArgumentException.class, () -> new Block(101, empty, null, 123, 321, 123));
    }

    @Test
    void getBlocks() {
        List<Hash> empty = List.of();
        List<Hash> one = List.of(Hash.empty());
        List<Hash> full = Stream
                .generate(Hash::empty)
                .limit(501)
                .toList();
        assertThrows(IllegalArgumentException.class, () -> new GetBlocks(121, null));
        assertThrows(IllegalArgumentException.class, () -> new GetBlocks(121, empty));
        assertThrows(IllegalArgumentException.class, () -> new GetBlocks(121, full));
        assertThrows(IllegalArgumentException.class, () -> new GetBlocks(121, one, null));
    }

    @Test
    void getHeaders() {
        List<Hash> empty = List.of();
        List<Hash> one = List.of(Hash.empty());
        List<Hash> full = Stream
                .generate(Hash::empty)
                .limit(2001)
                .toList();
        assertThrows(IllegalArgumentException.class, () -> new GetHeaders(121, null));
        assertThrows(IllegalArgumentException.class, () -> new GetHeaders(121, empty));
        assertThrows(IllegalArgumentException.class, () -> new GetHeaders(121, full));
        assertThrows(IllegalArgumentException.class, () -> new GetHeaders(121, one, null));
    }

    @Test
    void headers() {
        List<Block> empty = List.of();
        List<Block> one = List.of(new Block(-9999999,
                new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                new Hash("f8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                0,
                0,
                0,
                List.of(
                        new Tx(0,
                                List.of(
                                        new TxIn(new OutPoint(0, Hash.empty()), "AA", 0xff)
                                ),
                                List.of(
                                        new TxOut(123, "FFAAFa"),
                                        new TxOut(1239, "FFAAFa"),
                                        new TxOut(12399, "FFAAFa"),
                                        new TxOut(123999, "FFAAFa"),
                                        new TxOut(1239999, "FFAAFa"),
                                        new TxOut(12399999, "FFAAFa"),
                                        new TxOut(12399999999999L, "FF")
                                ),
                                0xff)
                )));
        assertThrows(IllegalArgumentException.class, () -> new Headers(null));
        assertThrows(IllegalArgumentException.class, () -> new Headers(empty));
        assertThrows(IllegalArgumentException.class, () -> new Headers(one));
    }

    @Test
    void inventoryPayload() {
        List<InventoryVector> empty = List.of();
        List<InventoryVector> full = Stream
                .generate(() -> new InventoryVector(InventoryVector.Type.MSG_TX, Hash.empty()))
                .limit(50_001)
                .toList();
        assertThrows(IllegalArgumentException.class, () -> new Inv(null));
        assertThrows(IllegalArgumentException.class, () -> new Inv(empty));
        assertThrows(IllegalArgumentException.class, () -> new Inv(full));
        assertThrows(IllegalArgumentException.class, () -> new GetData(null));
        assertThrows(IllegalArgumentException.class, () -> new GetData(empty));
        assertThrows(IllegalArgumentException.class, () -> new GetData(full));
        assertThrows(IllegalArgumentException.class, () -> new NotFound(null));
        assertThrows(IllegalArgumentException.class, () -> new NotFound(empty));
        assertThrows(IllegalArgumentException.class, () -> new NotFound(full));
    }

    @Test
    void reject() {
        assertThrows(IllegalArgumentException.class, () -> new Reject(Command.PING, Reject.Type.NONSTANDARD, null));
        assertThrows(IllegalArgumentException.class, () -> new Reject(Command.PING, null, "?"));
        assertThrows(IllegalArgumentException.class, () -> new Reject(null, Reject.Type.NONSTANDARD, "?"));
    }

    @Test
    void tx() {
        List<TxIn> emptyIn = List.of();
        List<TxIn> oneIn = List.of(new TxIn(new OutPoint(0, Hash.empty()), "abba", 0));
        List<TxOut> emptyOut = List.of();
        List<TxOut> oneOut = List.of(new TxOut(1, "abba"));
        assertThrows(IllegalArgumentException.class, () -> new Tx(1, null, oneOut, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tx(1, emptyIn, oneOut, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tx(1, oneIn, null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tx(1, oneIn, emptyOut, 0));

    }

    @Test
    void version() {
        Set<Service> empty = Set.of();
        NetworkAddress adr = new NetworkAddress(0, null, InetAddress.getLoopbackAddress(), 8080);
        assertDoesNotThrow(() -> new Version(1, null, 123, adr, adr, 321, "a", 33333, true));
        assertThrows(IllegalArgumentException.class, () -> new Version(1, empty, 123, null, adr, 321, "a", 33333, true));
        assertThrows(IllegalArgumentException.class, () -> new Version(1, empty, 123, adr, null, 321, "a", 33333, true));
    }
}