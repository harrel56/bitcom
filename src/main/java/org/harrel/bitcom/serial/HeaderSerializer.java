package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.PayloadType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HeaderSerializer extends Serializer {

    public static final int HEADER_SIZE = 24;
    public static final int COMMAND_SIZE = 12;

    public void serialize(Header header, OutputStream out) throws IOException {
        writeInt32LE(header.magicValue(), out);
        writeCommand(header.payloadType(), out);
        writeInt32LE(header.length(), out);
        writeInt32BE(header.checksum(), out);
    }

    public Header deserialize(InputStream in) throws IOException {
        int magicValue = readInt32LE(in.readNBytes(4));
        PayloadType payloadType = readCommand(in.readNBytes(12));
        int length = readInt32LE(in.readNBytes(4));
        int checksum = readInt32BE(in.readNBytes(4));
        return new Header(magicValue, payloadType, length, checksum);
    }

    private void writeCommand(PayloadType type, OutputStream out) throws IOException {
        byte[] commandBytes = type.name().getBytes(StandardCharsets.US_ASCII);
        out.write(Arrays.copyOf(commandBytes, COMMAND_SIZE));
    }

    private PayloadType readCommand(byte[] data) {
        int size;
        for (size = 0; size < data.length; size++) {
            if(data[size] == 0x0) {
                break;
            }
        }
        String command = new String(Arrays.copyOf(data, size));
        return PayloadType.valueOf(command);
    }
}
