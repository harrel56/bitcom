package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Tx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TxSerializer extends PayloadSerializer<Tx> {

    @Override
    public void serialize(Tx payload, OutputStream out) throws IOException {
        writeTx(payload, out);
    }

    @Override
    public Tx deserialize(InputStream in) throws IOException {
        return readTx(in);
    }
}
