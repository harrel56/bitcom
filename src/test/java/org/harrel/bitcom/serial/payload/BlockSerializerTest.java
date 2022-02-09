package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.OutPoint;
import org.harrel.bitcom.model.TxIn;
import org.harrel.bitcom.model.TxOut;
import org.harrel.bitcom.model.msg.payload.Block;
import org.harrel.bitcom.model.msg.payload.Tx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

class BlockSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Block[] data = new Block[]{
                new Block(70015,
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        new Hash("f8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        123321,
                        0xff,
                        0xfa41a0a1),
                new Block(-1,
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        new Hash("f8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        123321,
                        0xffffffff,
                        0),
                new Block(7015,
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        new Hash("f8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        12333321,
                        0xaa,
                        0xfa41a0a1),
                new Block(1016,
                        new Hash("68171c61b6859a0afffda63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        new Hash("f8171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df9"),
                        123322221,
                        0xffaaffaa,
                        0xfa41a0a1),
                new Block(-9999999,
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
                        ))
        };

        for (Block block : data) {
            new BlockSerializer().serialize(block, out);
            Block read = new BlockSerializer().deserialize(in);
            Assertions.assertEquals(block, read);
        }
    }
}