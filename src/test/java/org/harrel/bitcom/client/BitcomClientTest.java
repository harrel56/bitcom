package org.harrel.bitcom.client;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class BitcomClientTest {

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void connect() throws IOException {
        BitcomClient client = new BitcomClient("seed.bitcoin.sipa.be");
        Assertions.assertDoesNotThrow(client::connect);
    }

    @Test
    void sendMessage() throws IOException, ExecutionException, InterruptedException {
        BitcomClient client = new BitcomClient("seed.bitcoin.sipa.be");
        client.connect();

        CompletableFuture<Message<Version>> future = client.sendMessage(getVersionPayload());
        Assertions.assertDoesNotThrow(() -> future.get());
    }

    private Version getVersionPayload() throws UnknownHostException {
        return new Version(70015, 1024, System.currentTimeMillis() / 1000,
                new NetworkAddress(0, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                new NetworkAddress(1024, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                0x0, "", 1, false);
    }
}