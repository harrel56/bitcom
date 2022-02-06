package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.msg.payload.GetHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetHeadersSerializer extends PayloadSerializer<GetHeaders> {

    @Override
    public void serialize(GetHeaders payload, OutputStream out) throws IOException {
        writeInt32LE(payload.version(), out);
        writeVarInt(payload.hashes().size(), out);
        for (Hash hash : payload.hashes()) {
            writeHash(hash, out);
        }
        writeHash(payload.stopHash(), out);
    }

    @Override
    public GetHeaders deserialize(InputStream in) throws IOException {
        int version = readInt32LE(in);
        int count = (int) readVarInt(in);
        var hashes = new ArrayList<Hash>(count);
        for (int i = 0; i < count; i++) {
            hashes.add(readHash(in));
        }
        var stopHash = readHash(in);
        return new GetHeaders(version, List.copyOf(hashes), stopHash);
    }
}
