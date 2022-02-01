package org.harrel.bitcom.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends FilterInputStream {

    private final OutputStream teeOutput;

    public TeeInputStream(InputStream in, OutputStream teeOutput) {
        super(in);
        this.teeOutput = teeOutput;
    }

    @Override
    public int read() throws IOException {
        int read = super.read();
        teeOutput.write(read);
        return read;
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        byte[] bytes = super.readNBytes(len);
        teeOutput.write(bytes);
        return bytes;
    }

    @Override
    public int read(byte[] b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int available() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] readAllBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skipNBytes(long n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(OutputStream out) {
        throw new UnsupportedOperationException();
    }
}
