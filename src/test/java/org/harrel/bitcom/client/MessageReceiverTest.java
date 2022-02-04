package org.harrel.bitcom.client;

import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Version;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.harrel.bitcom.serial.SerializerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;

@Disabled
class MessageReceiverTest {

    PipedInputStream in;
    PipedOutputStream out;
    MessageReceiver receiver;

    @BeforeEach
    void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        receiver = new MessageReceiver(in, StandardConfiguration.MAIN, Listeners.builder().build());
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void receiverTest() throws Exception {


//        new HeaderSerializer().serialize(new Header(1, Command.version, 2, 3), out);
        Version version = new Version(7016, 1L, 321,
                new NetworkAddress(1, 1L, InetAddress.getByName("127.0.0.1"), 8181),
                new NetworkAddress(2, 2L, InetAddress.getByName("127.0.0.2"), 8282),
                123L, "user agent XXX", 22, true);

        ByteArrayOutputStream bout = new ByteArrayOutputStream(5000);
        for (int i = 0; i < 25; i++) {
            new SerializerFactory().getPayloadSerializer(version).serialize(version, bout);

        }

        bout.write(new byte[]{(byte) 249, (byte) 190, (byte) 180, (byte) 217});

        for (int i = 0; i < 25; i++) {
            new SerializerFactory().getPayloadSerializer(version).serialize(version, bout);

        }
        logger.debug("ALMOST");
        out.write(bout.toByteArray());

        logger.debug("is done boi");
//        listeners.addListener(Version.class, v -> {
//            try {
//                logger.info("in 1");
//                new HeaderSerializer().serialize(new Header(1, Command.version, 2, 3), out);
//                new SerializerFactory().getPayloadSerializer(version).serialize(version, out);
//                logger.info("out 1");
//            } catch (Exception e) {}
//        });
//        listeners.addListener(Version.class, v -> {
//            try {
//                logger.info("in 2");
//                new HeaderSerializer().serialize(new Header(1, Command.version, 2, 3), out);
//                new SerializerFactory().getPayloadSerializer(version).serialize(version, out);
//            } catch (Exception e) {}
//        });
//        new SerializerFactory().getPayloadSerializer(version).serialize(version, out);

        Thread.sleep(60000);
    }
}