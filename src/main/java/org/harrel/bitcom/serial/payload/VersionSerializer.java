package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Version;

import java.io.IOException;
import java.io.OutputStream;

public class VersionSerializer extends PayloadSerializer<Version> {

    @Override
    public int getExpectedByteSize() {
        return 86;
    }

    @Override
    public void serialize(Version version, OutputStream out) throws IOException {
        writeInt32LE(version.version(), out);
        writeInt64LE(version.services(), out);
        writeInt64LE(System.currentTimeMillis() / 1000, out);
        writeNetworkAddressWithoutTime(version.receiver(), out);
        writeNetworkAddressWithoutTime(version.transmitter(), out);
        writeInt64LE(version.nonce(), out);
        writeVarString(version.userAgent(), out);
        writeInt32LE(version.blockHeight(), out);
        out.write(version.relay() ? 0x01 : 0x00);
    }
}
