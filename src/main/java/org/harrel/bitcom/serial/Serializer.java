package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.NetworkAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.charset.StandardCharsets;

public abstract class Serializer {

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

    protected int readInt32LE(byte[] data) {
        int val = data[0];
        val |= data[1] << 8;
        val |= data[2] << 16;
        val |= data[3] << 24;
        return val;
    }

    protected int readInt32BE(byte[] data) {
        int val = data[0] << 24;
        val |= data[1] << 16;
        val |= data[2] << 8;
        val |= data[3];
        return val;
    }
}
