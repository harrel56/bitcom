package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.NetworkAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

class SerializerTest {

    PipedInputStream in;
    PipedOutputStream out;
    Serializer<?> mock;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        mock = Mockito.mock(Serializer.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    void int16LE() throws IOException {
        short[] data = {Short.MIN_VALUE, Short.MAX_VALUE, (short) 0xFFFFFFFE, (short) 0x00, (short) -1, (short) 2, (short) 0xD9B4BEF9};
        for (short num : data) {
            mock.writeInt16LE(num, out);
            Assertions.assertEquals(num, (short) mock.readInt16LE(in), "For value: " + Integer.toHexString(num));
        }
    }

    @Test
    void int16BE() throws IOException {
        short[] data = {Short.MIN_VALUE, Short.MAX_VALUE, (short) 0xFFFFFFFE, (short) 0x00, (short) -1, (short) 2, (short) 0xD9B4BEF9};
        for (short num : data) {
            mock.writeInt16BE(num, out);
            Assertions.assertEquals(num, (short) mock.readInt16BE(in), "For value: " + Integer.toHexString(num));
        }
    }

    @Test
    void int32LE() throws IOException {
        int[] data = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0xFFFFFFFE, 0x00, -1, 2, 0xD9B4BEF9};
        for (int num : data) {
            mock.writeInt32LE(num, out);
            Assertions.assertEquals(num, mock.readInt32LE(in), "For value: " + Integer.toHexString(num));
        }
    }

    @Test
    void int32BE() throws IOException {
        int[] data = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0xFFFFFFFE, 0x00, -1, 2, 0xD9B4BEF9};
        for (int num : data) {
            mock.writeInt32BE(num, out);
            Assertions.assertEquals(num, mock.readInt32BE(in), "For value: " + Integer.toHexString(num));
        }
    }

    @Test
    void int64LE() throws IOException {
        long[] data = {Long.MIN_VALUE, Long.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE,
                0xFFFFFFFE, 0x00, -1, 2, 0xD9B4BEF9, 0xD9B4BEF9D9B4BEF9L, 0x0102030405060708L};
        for (long num : data) {
            mock.writeInt64LE(num, out);
            Assertions.assertEquals(num, mock.readInt64LE(in), "For value: " + Long.toHexString(num));
        }
    }

    @Test
    void int64BE() throws IOException {
        long[] data = {Long.MIN_VALUE, Long.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE,
                0xFFFFFFFE, 0x00, -1, 2, 0xD9B4BEF9, 0xD9B4BEF9D9B4BEF9L, 0x0102030405060708L};
        for (long num : data) {
            mock.writeInt64BE(num, out);
            Assertions.assertEquals(num, mock.readInt64BE(in), "For value: " + Long.toHexString(num));
        }
    }

    @Test
    void varInt() throws IOException {
        long[] data = {Long.MIN_VALUE, Long.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE,
                0xFFFFFFFE, 0x00, -1, 2, 0xD9B4BEF9, 0xD9B4BEF9D9B4BEF9L, 0x0102030405060708L};
        for (long num : data) {
            mock.writeVarInt(num, out);
            Assertions.assertEquals(num, mock.readVarInt(in), "For value: " + Long.toHexString(num));
        }
    }

    @Test
    void writeVarInt() throws IOException {
        Map<Long, byte[]> data = new TreeMap<>(Map.ofEntries(
                Map.entry(0xBBL, new byte[]{(byte) 187}),
                Map.entry(0xFFL, new byte[]{(byte) 253, (byte) 255, (byte) 0}),
                Map.entry(0x3419L, new byte[]{(byte) 253, (byte) 25, (byte) 52}),
                Map.entry(0xDC4591L, new byte[]{(byte) 254, (byte) 145, (byte) 69, (byte) 220, (byte) 0}),
                Map.entry(0x80081E5L, new byte[]{(byte) 254, (byte) 229, (byte) 129, (byte) 0, (byte) 8}),
                Map.entry(0xB4DA564E2857L, new byte[]{(byte) 255, (byte) 87, (byte) 40, (byte) 78, (byte) 86, (byte) 218, (byte) 180, (byte) 0, (byte) 0}),
                Map.entry(0x4BF583A17D59C158L, new byte[]{(byte) 255, (byte) 88, (byte) 193, (byte) 89, (byte) 125, (byte) 161, (byte) 131, (byte) 245, (byte) 75})
        ));
        for (var entry : data.entrySet()) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(9);
            mock.writeVarInt(entry.getKey(), bout);
            Assertions.assertArrayEquals(entry.getValue(), bout.toByteArray(), "For value: " + Long.toHexString(entry.getKey()));
        }
    }

    @Test
    void readVarInt() throws IOException {
        Map<Long, byte[]> data = new TreeMap<>(Map.ofEntries(
                Map.entry(0xBBL, new byte[]{(byte) 187}),
                Map.entry(0xFFL, new byte[]{(byte) 253, (byte) 255, (byte) 0}),
                Map.entry(0x3419L, new byte[]{(byte) 253, (byte) 25, (byte) 52}),
                Map.entry(0xDC4591L, new byte[]{(byte) 254, (byte) 145, (byte) 69, (byte) 220, (byte) 0}),
                Map.entry(0x80081E5L, new byte[]{(byte) 254, (byte) 229, (byte) 129, (byte) 0, (byte) 8}),
                Map.entry(0xB4DA564E2857L, new byte[]{(byte) 255, (byte) 87, (byte) 40, (byte) 78, (byte) 86, (byte) 218, (byte) 180, (byte) 0, (byte) 0}),
                Map.entry(0x4BF583A17D59C158L, new byte[]{(byte) 255, (byte) 88, (byte) 193, (byte) 89, (byte) 125, (byte) 161, (byte) 131, (byte) 245, (byte) 75})
        ));
        for (var entry : data.entrySet()) {
            ByteArrayInputStream bin = new ByteArrayInputStream(entry.getValue());
            Assertions.assertEquals(entry.getKey(), mock.readVarInt(bin), "For value: " + Long.toHexString(entry.getKey()));
        }
    }

    @Test
    void varString() throws IOException {
        String[] data = new String[]{"", "1", " ", "\\//\\\n\r\t\f\b\b//\\", "21.0.1.8.654TR",
                "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM~!\"#$%&'()*+,-./0123456789:;<>=?@`{}|",
                "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM~!\"#$%&'()*+,-./0123456789:;<>=?@`{}qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM~!\"#$%&'()*+,-./0123456789:;<>=?@`{}qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM~!\"#$%&'()*+,-./0123456789:;<>=?@`{}"};
        for (String str : data) {
            mock.writeVarString(str, out);
            Assertions.assertEquals(str, mock.readVarString(in));
        }
    }

    @Test
    void networkAddress() throws IOException {
        NetworkAddress[] data = new NetworkAddress[]{
                new NetworkAddress(0, 0, InetAddress.getByName("127.0.0.1"), 80),
                new NetworkAddress(-255, 1024, InetAddress.getByName("8.8.8.8"), 443),
                new NetworkAddress(Integer.MAX_VALUE, Long.MAX_VALUE, InetAddress.getByName("localhost"), 0),
                new NetworkAddress(Integer.MIN_VALUE, Long.MIN_VALUE, InetAddress.getByName("2001:db8::1"), 0xffff),
                new NetworkAddress(1, 1, InetAddress.getByName("2001:db8:0:1:1:1:1:1"), 8080),
        };
        for (NetworkAddress adr : data) {
            mock.writeNetworkAddress(adr, out);
            Assertions.assertEquals(adr, mock.readNetworkAddress(in));
        }
    }

    @Test
    void networkAddressWithoutTime() throws IOException {
        NetworkAddress[] data = new NetworkAddress[]{
                new NetworkAddress(0, 0, InetAddress.getByName("127.0.0.1"), 80),
                new NetworkAddress(-255, 1024, InetAddress.getByName("8.8.8.8"), 443),
                new NetworkAddress(Integer.MAX_VALUE, Long.MAX_VALUE, InetAddress.getByName("localhost"), 0),
                new NetworkAddress(Integer.MIN_VALUE, Long.MIN_VALUE, InetAddress.getByName("2001:db8::1"), 0xffff),
                new NetworkAddress(1, 1, InetAddress.getByName("2001:db8:0:1:1:1:1:1"), 8080),
        };
        for (NetworkAddress adr : data) {
            mock.writeNetworkAddressWithoutTime(adr, out);
            NetworkAddress read = mock.readNetworkAddressWithoutTime(0, in);
            Assertions.assertEquals(0, read.time());
            Assertions.assertEquals(adr.services(), read.services());
            Assertions.assertEquals(adr.address(), read.address());
            Assertions.assertEquals(adr.port(), read.port());
        }
    }
}