package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.NetworkAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public abstract class Serializer<T> {

    public abstract void serialize(T payload, OutputStream out) throws IOException;

    public abstract T deserialize(InputStream in) throws IOException;

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
        if (val < 0xFD) {
            out.write((int) val);
        } else if (val < 0xFFFF) {
            out.write(0xFD);
            writeInt16LE((int) val, out);
        } else if (val < 0xFFFF_FFFFL) {
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

    protected void writeNetworkAddress(NetworkAddress address, OutputStream out) throws IOException {
        writeInt32LE((int) (System.currentTimeMillis() / 1000), out);
        writeNetworkAddressWithoutTime(address, out);
    }

    protected void writeNetworkAddressWithoutTime(NetworkAddress address, OutputStream out) throws IOException {
        writeInt64LE(address.services(), out);

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

    protected int readInt16LE(byte[] data) {
        int val = data[0] & 0xFF;
        val |= (data[1] & 0xFF) << 8;
        return val;
    }

    protected int readInt16BE(byte[] data) {
        int val = (data[0] & 0xFF) << 8;
        val |= data[1] & 0xFF;
        return val;
    }

    protected int readInt32LE(byte[] data) {
        int val = data[0] & 0xFF;
        val |= (data[1] & 0xFF) << 8;
        val |= (data[2] & 0xFF) << 16;
        val |= (data[3] & 0xFF) << 24;
        return val;
    }

    protected int readInt32BE(byte[] data) {
        int val = (data[0] & 0xFF) << 24;
        val |= (data[1] & 0xFF)<< 16;
        val |= (data[2] & 0xFF)<< 8;
        val |= data[3] & 0xFF;
        return val;
    }

    protected long readInt64LE(byte[] data) {
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

    protected long readInt64BE(byte[] data) {
        long val = (long) (data[0] & 0xFF) << 56;
        val |= (long) (data[1] & 0xFF) << 48;
        val |= (long) (data[2] & 0xFF) << 40;
        val |= (long) (data[3] & 0xFF) << 32;
        val |= (long) (data[4] & 0xFF) << 24;
        val |= (data[5] & 0xFF) << 16;
        val |= (data[6] & 0xFF) << 8;
        val |= data[7];
        return val;
    }

    protected long readVarInt(InputStream in) throws IOException {
        int indicator = in.read();
        if (indicator < 0xFD) {
            return indicator;
        } else if (indicator == 0xFD) {
            return readInt16LE(in.readNBytes(2));
        } else if (indicator == 0xFE) {
            return readInt32LE(in.readNBytes(4));
        } else {
            return readInt64LE(in.readNBytes(8));
        }
    }

    protected String readVarString(InputStream in) throws IOException {
        int length = (int) readVarInt(in);
        return new String(in.readNBytes(length), StandardCharsets.US_ASCII);
    }

    protected NetworkAddress readNetworkAddress(InputStream in) throws IOException {
        int time = readInt32LE(in.readNBytes(4));
        return readNetworkAddressWithoutTime(time, in);
    }

    protected NetworkAddress readNetworkAddressWithoutTime(int time, InputStream in) throws IOException {
        long services = readInt64LE(in.readNBytes(8));
        InetAddress address = InetAddress.getByAddress(in.readNBytes(16));
        int port = readInt16BE(in.readNBytes(2));
        return new NetworkAddress(time, services, address, port);
    }
}
