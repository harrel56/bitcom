package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.msg.payload.Block;
import org.harrel.bitcom.model.msg.payload.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

class HeadersSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        Headers[] data = new Headers[]{
                new Headers(
                        List.of(
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
                                        0)
                        )
                )};

        for (Headers headers : data) {
            new HeadersSerializer().serialize(headers, out);
            Headers read = new HeadersSerializer().deserialize(in);
            Assertions.assertEquals(headers, read);
        }
    }
}