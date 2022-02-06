package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Addr;
import org.harrel.bitcom.model.msg.payload.GetBlocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.util.List;

class GetBlocksSerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        GetBlocks[] data = new GetBlocks[]{
                new GetBlocks(70015, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6")
                )),
                new GetBlocks(1019, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6")
                ), Hash.empty()),
                new GetBlocks(0, List.of(
                        new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b42de3e03008aed4e4181a31e1fff418cb7df6"),
                        new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6")
                ), new Hash("68171c61b6859a0a355da63bd5b32de3e03008aed4e4181a31e1fff418cb7df6"))
        };
        for (GetBlocks block : data) {
            new GetBlocksSerializer().serialize(block, out);
            Assertions.assertEquals(block, new GetBlocksSerializer().deserialize(in));
        }
    }
}
