package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HeaderSerializer extends Serializer<Header> {

    public static final int HEADER_SIZE = 24;
    public static final int MAGIC_SIZE = 4;
    public static final int COMMAND_SIZE = 12;

    @Override
    public void serialize(Header header, OutputStream out) throws IOException {
        writeInt32LE(header.magicValue(), out);
        writeCommand(header.command(), out);
        writeInt32LE(header.length(), out);
        writeInt32BE(header.checksum(), out);
    }

    @Override
    public Header deserialize(InputStream in) throws IOException {
        int magicValue = readInt32LE(in);
        Command command = readCommand(in);
        int length = readInt32LE(in);
        int checksum = readInt32BE(in);
        return new Header(magicValue, command, length, checksum);
    }

    public byte[] serializeMagicValueAsBytes(int val) {
        var bout = new ByteArrayOutputStream(4);
        try {
            writeInt32LE(val, bout);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bout.toByteArray();
    }

    private void writeCommand(Command type, OutputStream out) throws IOException {
        byte[] commandBytes = type.name().toLowerCase().getBytes(StandardCharsets.US_ASCII);
        out.write(Arrays.copyOf(commandBytes, COMMAND_SIZE));
    }

    private Command readCommand(InputStream in) throws IOException {
        byte[] data = in.readNBytes(COMMAND_SIZE);
        int size;
        for (size = 0; size < data.length; size++) {
            if (data[size] == 0x0) {
                break;
            }
        }
        String command = new String(Arrays.copyOf(data, size));
        return Command.valueOf(command.toUpperCase());
    }
}
