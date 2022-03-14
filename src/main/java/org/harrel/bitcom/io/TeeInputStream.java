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
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        teeOutput.write(b, off, read);
        return read;
    }
}
