package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.msg.payload.GetBlocks;
import org.harrel.bitcom.model.msg.payload.GetHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

class GetHeadersSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        GetHeaders[] data = new GetHeaders[]{
                new GetHeaders(70015, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6")
                )),
                new GetHeaders(1019, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6")
                ), Hash.empty()),
                new GetHeaders(0, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6")
                ), new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6"))
        };
        for (GetHeaders header : data) {
            new GetHeadersSerializer().serialize(header, out);
            Assertions.assertEquals(header, new GetHeadersSerializer().deserialize(in));
        }
    }
}
