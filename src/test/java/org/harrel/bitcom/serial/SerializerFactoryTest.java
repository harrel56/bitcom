package org.harrel.bitcom.serial;

import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SerializerFactoryTest {

    @Test
    void getHeaderSerializer() {
        Assertions.assertNotNull(new SerializerFactory().getHeaderSerializer());
    }

    @Test
    void getPayloadSerializerGeneric() {
        for (Command cmd : Command.values()) {
            Payload mock = Mockito.mock(Payload.class);
            Mockito.when(mock.getCommand()).thenReturn(cmd);
            Assertions.assertNotNull(new SerializerFactory().getPayloadSerializer(mock));
        }
    }

    @Test
    void getPayloadSerializerInterface() {
        for (Command cmd : Command.values()) {
            PayloadSerializer<?> serial = new SerializerFactory().getPayloadSerializer(cmd);
            Assertions.assertNotNull(serial);
        }
    }
}