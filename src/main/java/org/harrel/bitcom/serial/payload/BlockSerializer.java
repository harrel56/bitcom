package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BlockSerializer extends PayloadSerializer<Block> {
    @Override
    public void serialize(Block payload, OutputStream out) throws IOException {
        writeBlock(payload, out);
    }

    @Override
    public Block deserialize(InputStream in) throws IOException {
        return readBlock(in);
    }
}
