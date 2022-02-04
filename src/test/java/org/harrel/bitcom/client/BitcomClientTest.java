package org.harrel.bitcom.client;

import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class BitcomClientTest {

    static BitcomServer server;

    @BeforeAll
    static void init() throws IOException {
        server = new BitcomServer(8333);
    }

    @Test
    void build() throws IOException {
        BitcomClient client = BitcomClient.builder().withAddress("127.0.0.1")
                .withNetworkConfiguration(StandardConfiguration.MAIN)
                .buildAndConnect();
        Assertions.assertNotNull(client);
    }

    @Test
    void defaultFields() throws IOException {
        BitcomClient client = BitcomClient.builder().buildAndConnect();
        Assertions.assertNotNull(client.getAddress());
        Assertions.assertNotNull(client.getNetworkConfiguration());
    }

    @Test
    void sendMessage() throws IOException {
        BitcomClient client = BitcomClient.builder()
                .withAddress(InetAddress.getLocalHost())
                .buildAndConnect();

        var future = client.sendMessage(getVersionPayload());
        Assertions.assertDoesNotThrow(() -> future.get());
    }

    @Test
    void close() throws IOException {
        BitcomClient clientOuter;
        try (BitcomClient client = BitcomClient.builder().buildAndConnect()) {
            clientOuter = client;
        }
        Assertions.assertTrue(clientOuter.isClosed());
    }

    @Test
    void receiveMessage() throws Exception {
        CompletableFuture<Payload> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withGlobalListener((c, p) -> receiveFuture.complete(p))
                .buildAndConnect();
        Version data = getVersionPayload();
        server.send(serializeMessage(data));
        Assertions.assertEquals(data, receiveFuture.get(300, TimeUnit.MILLISECONDS));
    }

    @Test
    void closeClientInCallback() throws Exception {
        CompletableFuture<Payload> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withGlobalListener((c, p) -> {
                    try {
                        c.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .buildAndConnect();
        Version data = getVersionPayload();
        server.send(serializeMessage(data));
        Assertions.assertEquals(data, receiveFuture.get(300, TimeUnit.MILLISECONDS));
    }

    private <T extends Payload> byte[] serializeMessage(T payload) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageSender sender = new MessageSender(bout, StandardConfiguration.MAIN);
        sender.sendMessage(payload).get();
        return bout.toByteArray();
    }

    private Version getVersionPayload() throws UnknownHostException {
        return new Version(70015, 1024, System.currentTimeMillis() / 1000,
                new NetworkAddress(0, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                new NetworkAddress(0, 0x00, InetAddress.getByName("127.0.0.1"), 8333),
                0x0, "", 1, false);
    }
}