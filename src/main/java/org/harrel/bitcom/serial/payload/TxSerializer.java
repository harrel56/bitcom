package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.TxIn;
import org.harrel.bitcom.model.TxOut;
import org.harrel.bitcom.model.msg.payload.Tx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TxSerializer extends PayloadSerializer<Tx> {

    @Override
    public void serialize(Tx payload, OutputStream out) throws IOException {
        writeInt32LE(payload.version(), out);
        writeVarInt(payload.inputs().size(), out);
        for (TxIn input : payload.inputs()) {
            writeTxIn(input, out);
        }
        writeVarInt(payload.outputs().size(), out);
        for (TxOut output : payload.outputs()) {
            writeTxOut(output, out);
        }
        writeInt32LE(payload.lockTime(), out);
    }

    @Override
    public Tx deserialize(InputStream in) throws IOException {
        int version = readInt32LE(in);
        int inputsCount = (int) readVarInt(in);
        List<TxIn> inputs = new ArrayList<>(inputsCount);
        for (int i = 0; i < inputsCount; i++) {
            inputs.add(readTxIn(in));
        }
        int outputsCount = (int) readVarInt(in);
        List<TxOut> outputs = new ArrayList<>(outputsCount);
        for (int i = 0; i < outputsCount; i++) {
            outputs.add(readTxOut(in));
        }
        int lockTime = readInt32LE(in);
        return new Tx(version, inputs, outputs, lockTime);
    }
}
