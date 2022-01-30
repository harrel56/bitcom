package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.PayloadType;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MessageReceiverTest {

    PipedInputStream in;
    PipedOutputStream out;
    MessageReceiver receiver;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        receiver = new MessageReceiver(in);
    }

    @Test
    void startListening() {
        assertDoesNotThrow(receiver::startListening);
    }

    @Test
    void startingTwice() {
        receiver.startListening();
        assertThrows(IllegalStateException.class, receiver::startListening);
    }

    @Test
    void stop() {
        receiver.startListening();
        assertDoesNotThrow(receiver::stop);
    }

    @Test
    void stoppingTwice() {
        receiver.startListening();
        receiver.stop();
        assertThrows(IllegalStateException.class, receiver::stop);
    }

    @Test
    void stopBeforeStart() {
        assertThrows(IllegalStateException.class, receiver::stop);
    }

    @Test
    void receiverTest() throws Exception {
        receiver.startListening();
        new HeaderSerializer().serialize(new Header(1, PayloadType.ping, 2, 3), out);
    }

}