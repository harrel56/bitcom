package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.msg.payload.Addr;
import org.harrel.bitcom.model.msg.payload.GetBlocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetBlocksSerializer extends PayloadSerializer<GetBlocks> {

    @Override
    public void serialize(GetBlocks payload, OutputStream out) throws IOException {
        writeInt32LE(payload.version(), out);
        writeVarInt(payload.hashes().size(), out);
        for (Hash hash : payload.hashes()) {
            writeHash(hash, out);
        }
        writeHash(payload.stopHash(), out);
    }

    @Override
    public GetBlocks deserialize(InputStream in) throws IOException {
        int version = readInt32LE(in);
        int count = (int) readVarInt(in);
        if(count > GetBlocks.SIZE_RANGE.max()) {
            throw new IllegalArgumentException("GetBlocks message must contain valid number of block hashes " + GetBlocks.SIZE_RANGE);
        }
        var hashes = new ArrayList<Hash>(count);
        for (int i = 0; i < count; i++) {
            hashes.add(readHash(in));
        }
        var stopHash = readHash(in);
        return new GetBlocks(version, List.copyOf(hashes), stopHash);
    }
}
