package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.Hash;
import org.harrel.bitcom.model.InventoryVector;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.*;
import org.harrel.bitcom.serial.SerializerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.util.List;

class InventorySerializerTest {

    PipedInputStream in;
    PipedOutputStream out;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
    }

    @Test
    void serializeLoop() throws IOException {
        InventoryPayload[] data = new InventoryPayload[]{
                new Inv(List.of(
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9018bb7c1ee7c591e91ed98ba364d449db3e817e5a919672c16d211a04c584fa")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("4ae8c9ad3a9f1979f444b6ce0e94ed26cd5343303e08165b7ffe2df55b439e60")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9883e273073e2b9f40c3d958b7aadac54b38b84b4d373d1c0a3ba9e6b7076f9c")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("773382437a35a4d848515e6d98ff0c127b878c5bc71fd03444484c6e9bb705a7")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("ef3d47407d8c94faddda1160f8bf2a9caf78f200ea25244e166cb15e071d988c")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.ERROR, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_WITNESS_TX, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_WITNESS_BLOCK, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_TX, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_FILTERED_WITNESS_BLOCK, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_FILTERED_BLOCK, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_CMPCT_BLOCK, new Hash("9e409413d3683e0bf110d0183f6f73b588eb9f1d36dcbbc33ad193be09cb1cab")),
                        new InventoryVector(InventoryVector.Type.MSG_BLOCK, new Hash("000000000000000000000e8e28a4ce4c7a882bc893e702000000000000000000"))
                )),
                new GetData(List.of(
                        new InventoryVector(InventoryVector.Type.MSG_BLOCK, new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6")),
                        new InventoryVector(InventoryVector.Type.MSG_TX, new Hash("9018bb7c1ee7c591e91ed98ba364d449db3e817e5a919672c16d211a04c584fa")),
                        new InventoryVector(InventoryVector.Type.MSG_TX, new Hash("4ae8c9ad3a9f1979f444b6ce0e94ed26cd5343303e08165b7ffe2df55b439e60")),
                        new InventoryVector(InventoryVector.Type.MSG_TX, new Hash("ef3d47407d8c94faddda1160f8bf2a9caf78f200ea25244e166cb15e071d988c")),
                        new InventoryVector(InventoryVector.Type.MSG_TX, new Hash("000000000000000000000e8e28a4ce4c7a882bc893e702000000000000000000"))
                )),
                new NotFound(List.of(
                        new InventoryVector(InventoryVector.Type.MSG_BLOCK, new Hash("68171c61b6859a0a355da63bd5b12de3e03008aed4e4181a31e1fff418cb7df6"))
                ))
        };
        for (InventoryPayload inv : data) {
            var serializer = new SerializerFactory().getPayloadSerializer(inv);
            serializer.serialize(inv, out);
            Assertions.assertEquals(inv, serializer.deserialize(in));
        }
    }
}
