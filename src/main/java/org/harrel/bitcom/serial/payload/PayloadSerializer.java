package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.TxIn;
import org.harrel.bitcom.model.TxOut;
import org.harrel.bitcom.model.msg.payload.Block;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.model.msg.payload.Tx;
import org.harrel.bitcom.serial.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class PayloadSerializer<T extends Payload> extends Serializer<T> {

    public void writeBlock(Block payload, OutputStream out) throws IOException {
        writeInt32LE(payload.version(), out);
        writeHash(payload.previous(), out);
        writeHash(payload.merkleRoot(), out);
        writeInt32LE(payload.timestamp(), out);
        writeInt32LE(payload.bits(), out);
        writeInt32LE(payload.nonce(), out);
        writeVarInt(payload.transactions().size(), out);
        for (Tx tx : payload.transactions()) {
            writeTx(tx, out);
        }
    }

    public Block readBlock(InputStream in) throws IOException {
        int version = readInt32LE(in);
        Hash previous = readHash(in);
        Hash merkleRoot = readHash(in);
        int timestamp = readInt32LE(in);
        int bits = readInt32LE(in);
        int nonce = readInt32LE(in);
        int txCount = (int) readVarInt(in);
        List<Tx> txs = new ArrayList<>(txCount);
        for (int i = 0; i < txCount; i++) {
            txs.add(readTx(in));
        }
        return new Block(version, previous, merkleRoot, timestamp, bits, nonce, List.copyOf(txs));
    }

    protected void writeTx(Tx payload, OutputStream out) throws IOException {
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

    protected Tx readTx(InputStream in) throws IOException {
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
        return new Tx(version, List.copyOf(inputs), List.copyOf(outputs), lockTime);
    }
}
