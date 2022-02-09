package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Block;
import org.harrel.bitcom.model.msg.payload.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HeadersSerializer extends PayloadSerializer<Headers> {
    @Override
    public void serialize(Headers payload, OutputStream out) throws IOException {
        writeVarInt(payload.blocks().size(), out);
        for (Block block : payload.blocks()) {
            writeBlock(block, out);
        }
    }

    @Override
    public Headers deserialize(InputStream in) throws IOException {
        int blockCount = (int) readVarInt(in);
        List<Block> blocks = new ArrayList<>(blockCount);
        for (int i = 0; i < blockCount; i++) {
            blocks.add(readBlock(in));
        }
        return new Headers(List.copyOf(blocks));
    }
}
