package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.OutPoint;
import org.harrel.bitcom.model.TxIn;
import org.harrel.bitcom.model.TxOut;
import org.harrel.bitcom.model.msg.payload.Ping;
import org.harrel.bitcom.model.msg.payload.Tx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

class TxSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Hash h1 = new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6");
        Tx[] data = new Tx[] {
                new Tx(7015,
                        List.of(
                                new TxIn(new OutPoint(0, h1), "AA", 0xff)
                        ),
                        List.of(
                                new TxOut(123, "FFAAFa")
                        ),
                        0),
                new Tx(0,
                        List.of(
                                new TxIn(new OutPoint(0, h1), "AA", 0xff)
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
                        0xff),
                new Tx(-1,
                        List.of(
                                new TxIn(new OutPoint(0, h1), "AA", 0xff),
                                new TxIn(new OutPoint(1, h1), "AA", 0xff),
                                new TxIn(new OutPoint(2, h1), "AA", 0xff),
                                new TxIn(new OutPoint(3, h1), "AA", 0xff)
                        ),
                        List.of(
                                new TxOut(123, "FFAAFa")
                        ),
                        0xfafafafa),

        };

        for (Tx tx : data) {
            new TxSerializer().serialize(tx, out);
            Tx read = new TxSerializer().deserialize(in);
            Assertions.assertEquals(tx, read);
        }
    }
}