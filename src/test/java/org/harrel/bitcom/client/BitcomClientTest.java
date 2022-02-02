package org.harrel.bitcom.client;

import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

class BitcomClientTest {

    @Test
    void connect() throws IOException {
        BitcomClient client = new BitcomClient(StandardConfiguration.MAIN.getDnsSeeders()[0], StandardConfiguration.MAIN);
        Assertions.assertDoesNotThrow(client::connect);
    }

    @Test
    void sendMessage() throws IOException {
        BitcomClient client = new BitcomClient(StandardConfiguration.MAIN.getDnsSeeders()[0], StandardConfiguration.MAIN);
        client.connect();

        var future = client.sendMessage(getVersionPayload());
        Assertions.assertDoesNotThrow(() -> future.get());
    }

    private Version getVersionPayload() throws UnknownHostException {
        return new Version(70015, 1024, System.currentTimeMillis() / 1000,
                new NetworkAddress(0, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                new NetworkAddress(1024, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                0x0, "", 1, false);
    }
}