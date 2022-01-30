package org.harrel.bitcom.client;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Version;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.harrel.bitcom.serial.SerializerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageReceiverTest {

    PipedInputStream in;
    PipedOutputStream out;
    Listeners listeners;
    MessageReceiver receiver;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        listeners = new Listeners();
        receiver = new MessageReceiver(in, listeners);
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

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void receiverTest() throws Exception {

        receiver.startListening();
        new HeaderSerializer().serialize(new Header(1, Command.version, 2, 3), out);
        Version version = new Version(7016, 1L, 321,
                new NetworkAddress(1, 1L, InetAddress.getByName("127.0.0.1"), 8181),
                new NetworkAddress(2, 2L, InetAddress.getByName("127.0.0.2"), 8282),
                123L, "user agent XXX", 22, true);

        listeners.addListener(Version.class, v -> {
            try {
                logger.info("in");
                new HeaderSerializer().serialize(new Header(1, Command.version, 2, 3), out);
                new SerializerFactory().getPayloadSerializer(version).serialize(version, out);
            } catch (Exception e) {}
        });
        new SerializerFactory().getPayloadSerializer(version).serialize(version, out);

        Thread.sleep(60000);
    }
}