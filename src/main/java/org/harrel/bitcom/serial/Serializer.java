package org.harrel.bitcom.serial;

import org.harrel.bitcom.client.Hashes;
import org.harrel.bitcom.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Serializer<T> {

    protected static final int VAR_INT_SIZE = 9;
    protected static final int VAR_STRING_SIZE = 30;
    protected static final int HASH_SIZE = 32;

    public abstract void serialize(T payload, OutputStream out) throws IOException;

    public abstract T deserialize(InputStream in) throws IOException;

    protected byte[] reverseBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte tmp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = tmp;
        }
        return bytes;
    }

    protected void writeInt16LE(int val, OutputStream out) throws IOException {
        out.write(val);
        out.write(val >> 8);
    }

    protected void writeInt16BE(int val, OutputStream out) throws IOException {
        out.write(val >> 8);
        out.write(val);
    }

    protected void writeInt32LE(int val, OutputStream out) throws IOException {
        out.write(val);
        out.write(val >> 8);
        out.write(val >> 16);
        out.write(val >> 24);
    }

    protected void writeInt32BE(int val, OutputStream out) throws IOException {
        out.write(val >> 24);
        out.write(val >> 16);
        out.write(val >> 8);
        out.write(val);
    }

    protected void writeInt64LE(long val, OutputStream out) throws IOException {
        out.write((int) val);
        out.write((int) (val >> 8));
        out.write((int) (val >> 16));
        out.write((int) (val >> 24));
        out.write((int) (val >> 32));
        out.write((int) (val >> 40));
        out.write((int) (val >> 48));
        out.write((int) (val >> 56));
    }

    protected void writeInt64BE(long val, OutputStream out) throws IOException {
        out.write((int) (val >> 56));
        out.write((int) (val >> 48));
        out.write((int) (val >> 40));
        out.write((int) (val >> 32));
        out.write((int) (val >> 24));
        out.write((int) (val >> 16));
        out.write((int) (val >> 8));
        out.write((int) val);
    }

    protected void writeVarInt(long val, OutputStream out) throws IOException {
        if (val >= 0x0 && val < 0xFD) {
            out.write((int) val);
        } else if (val >= 0xFD && val < 0xFFFF) {
            out.write(0xFD);
            writeInt16LE((int) val, out);
        } else if (val >= 0xFFFF && val < 0xFFFF_FFFFL) {
            out.write(0xFE);
            writeInt32LE((int) val, out);
        } else {
            out.write(0xFF);
            writeInt64LE(val, out);
        }
    }

    protected void writeVarString(String val, OutputStream out) throws IOException {
        writeVarInt(val.length(), out);
        out.write(val.getBytes(StandardCharsets.US_ASCII));
    }

    protected void writeServices(Set<Service> services, OutputStream out) throws IOException {
        long val = 0L;
        for (Service service : services) {
            val |= service.getValue();
        }
        writeInt64LE(val, out);
    }

    protected void writeNetworkAddress(NetworkAddress address, OutputStream out) throws IOException {
        writeInt32LE(address.time(), out);
        writeNetworkAddressWithoutTime(address, out);
    }

    protected void writeNetworkAddressWithoutTime(NetworkAddress address, OutputStream out) throws IOException {
        writeServices(address.services(), out);

        if (address.address() instanceof Inet4Address ipv4) {
            writeInt64BE(0x00L, out);
            writeInt32BE(0x0000ffff, out);
            if (ipv4.getAddress().length != 4) {
                throw new IllegalArgumentException("Inet4Address unexpected byte size");
            }
            out.write(ipv4.getAddress());
        } else if (address.address() instanceof Inet6Address ipv6) {
            if (ipv6.getAddress().length != 16) {
                throw new IllegalArgumentException("Inet6Address unexpected byte size");
            }
            out.write(ipv6.getAddress());
        } else {
            throw new IllegalArgumentException("InetAddress was null or wrong type");
        }

        writeInt16BE(address.port(), out);
    }

    protected void writeInventoryVector(InventoryVector vector, OutputStream out) throws IOException {
        writeInt32LE(vector.type().getValue(), out);
        writeHash(vector.hash(), out);
    }

    protected void writeHash(Hash hash, OutputStream out) throws IOException {
        byte[] hashBytes = Hashes.decodeHex(hash.value());
        out.write(Arrays.copyOf(reverseBytes(hashBytes), HASH_SIZE));
    }

    protected void writeTxIn(TxIn txIn, OutputStream out) throws IOException {
        writeOutPoint(txIn.previous(), out);
        byte[] scriptBytes = Hashes.decodeHex(txIn.script());
        writeVarInt(scriptBytes.length, out);
        out.write(scriptBytes);
        writeInt32LE(txIn.sequence(), out);
    }

    protected void writeTxOut(TxOut txOut, OutputStream out) throws IOException {
        writeInt64LE(txOut.value(), out);
        byte[] scriptBytes = Hashes.decodeHex(txOut.script());
        writeVarInt(scriptBytes.length, out);
        out.write(scriptBytes);
    }

    protected void writeOutPoint(OutPoint outPoint, OutputStream out) throws IOException {
        writeHash(outPoint.hash(), out);
        writeInt32LE(outPoint.index(), out);
    }

    protected int readInt16LE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(2);
        int val = data[0] & 0xFF;
        val |= (data[1] & 0xFF) << 8;
        return val;
    }

    protected int readInt16BE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(2);
        int val = (data[0] & 0xFF) << 8;
        val |= data[1] & 0xFF;
        return val;
    }

    protected int readInt32LE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(4);
        int val = data[0] & 0xFF;
        val |= (data[1] & 0xFF) << 8;
        val |= (data[2] & 0xFF) << 16;
        val |= (data[3] & 0xFF) << 24;
        return val;
    }

    protected int readInt32BE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(4);
        int val = (data[0] & 0xFF) << 24;
        val |= (data[1] & 0xFF) << 16;
        val |= (data[2] & 0xFF) << 8;
        val |= data[3] & 0xFF;
        return val;
    }

    protected long readInt64LE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(8);
        long val = data[0] & 0xFF;
        val |= (data[1] & 0xFF) << 8;
        val |= (data[2] & 0xFF) << 16;
        val |= (long) (data[3] & 0xFF) << 24;
        val |= (long) (data[4] & 0xFF) << 32;
        val |= (long) (data[5] & 0xFF) << 40;
        val |= (long) (data[6] & 0xFF) << 48;
        val |= (long) (data[7] & 0xFF) << 56;
        return val;
    }

    protected long readInt64BE(InputStream in) throws IOException {
        byte[] data = in.readNBytes(8);
        long val = (long) (data[0] & 0xFF) << 56;
        val |= (long) (data[1] & 0xFF) << 48;
        val |= (long) (data[2] & 0xFF) << 40;
        val |= (long) (data[3] & 0xFF) << 32;
        val |= (long) (data[4] & 0xFF) << 24;
        val |= (data[5] & 0xFF) << 16;
        val |= (data[6] & 0xFF) << 8;
        val |= data[7] & 0xFF;
        return val;
    }

    protected long readVarInt(InputStream in) throws IOException {
        int indicator = in.read();
        if (indicator < 0xFD) {
            return indicator;
        } else if (indicator == 0xFD) {
            return readInt16LE(in);
        } else if (indicator == 0xFE) {
            return readInt32LE(in);
        } else {
            return readInt64LE(in);
        }
    }

    protected String readVarString(InputStream in) throws IOException {
        int length = (int) readVarInt(in);
        return new String(in.readNBytes(length), StandardCharsets.US_ASCII);
    }

    protected Set<Service> readServices(InputStream in) throws IOException {
        Set<Service> services = new HashSet<>();
        long val = readInt64LE(in);
        for (Service service : Service.values()) {
            if ((val & service.getValue()) == service.getValue()) {
                services.add(service);
            }
        }
        return Set.copyOf(services);
    }

    protected NetworkAddress readNetworkAddress(InputStream in) throws IOException {
        int time = readInt32LE(in);
        return readNetworkAddressWithoutTime(time, in);
    }

    protected NetworkAddress readNetworkAddressWithoutTime(int time, InputStream in) throws IOException {
        var services = readServices(in);
        InetAddress address = InetAddress.getByAddress(in.readNBytes(16));
        int port = readInt16BE(in);
        return new NetworkAddress(time, services, address, port);
    }

    protected InventoryVector readInventoryVector(InputStream in) throws IOException {
        InventoryVector.Type type = InventoryVector.Type.forValue(readInt32LE(in));
        Hash hash = readHash(in);
        return new InventoryVector(type, hash);
    }

    protected Hash readHash(InputStream in) throws IOException {
        byte[] hashBE = reverseBytes(in.readNBytes(HASH_SIZE));
        return new Hash(Hashes.encodeHex(hashBE));
    }

    protected TxIn readTxIn(InputStream in) throws IOException {
        OutPoint previous = readOutPoint(in);
        int scriptLength = (int) readVarInt(in);
        byte[] scriptBytes = in.readNBytes(scriptLength);
        String script = Hashes.encodeHex(scriptBytes);
        int sequence = readInt32LE(in);
        return new TxIn(previous, script, sequence);
    }

    protected TxOut readTxOut(InputStream in) throws IOException {
        long value = readInt64LE(in);
        int scriptLength = (int) readVarInt(in);
        byte[] scriptBytes = in.readNBytes(scriptLength);
        String script = Hashes.encodeHex(scriptBytes);
        return new TxOut(value, script);
    }

    protected OutPoint readOutPoint(InputStream in) throws IOException {
        Hash hash = readHash(in);
        int index = readInt32LE(in);
        return new OutPoint(index, hash);
    }

}
