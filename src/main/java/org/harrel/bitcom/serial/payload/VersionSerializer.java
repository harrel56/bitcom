package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VersionSerializer extends PayloadSerializer<Version> {

    @Override
    public int getExpectedByteSize() {
        return 4 + // version
                8 + // services
                8 + // timestamp
                26 + // addr_recv
                26 + // addr_from
                8 + // nonce
                VAR_STRING_SIZE + // user_agent
                4 + // start_height
                1; // relay
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

    @Override
    public Version deserialize(InputStream in) throws IOException {
        int version = readInt32LE(in);
        long services = readInt64LE(in);
        long timestamp = readInt64LE(in);
        NetworkAddress receiver = readNetworkAddressWithoutTime(-1, in);
        NetworkAddress transmitter = readNetworkAddressWithoutTime(-1, in);
        long nonce = readInt64LE(in);
        String userAgent = readVarString(in);
        int blockHeight = readInt32LE(in);
        boolean relay = in.read() != 0x00;
        return new Version(version, services, timestamp, receiver, transmitter, nonce, userAgent, blockHeight, relay);
    }
}
