package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Tag("functional")
class BitcomClientTest {

    BitcomServer server;
    InetAddress localAddress;

    @BeforeEach
    void init() throws IOException {
        server = new BitcomServer(8333);
        localAddress = InetAddress.getByName("127.0.0.1");
    }

    @AfterEach
    void tearDown() throws IOException {
        server.close();
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
    void sendMsg() throws IOException {
        BitcomClient client = BitcomClient.builder()
                .withAddress(InetAddress.getLocalHost())
                .buildAndConnect();

        var future = client.sendMessage(getVersionPayload());
        Assertions.assertDoesNotThrow(() -> future.get());
    }

    @Test
    void sendNullMsg() throws IOException {
        BitcomClient client = BitcomClient.builder()
                .withAddress(InetAddress.getLocalHost())
                .buildAndConnect();

        Assertions.assertThrows(IllegalArgumentException.class, () -> client.sendMessage(null));
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
    void closeAndSendMsg() throws IOException {
        BitcomClient clientOuter;
        try (BitcomClient client = BitcomClient.builder().buildAndConnect()) {
            clientOuter = client;
        }
        Version payload = getVersionPayload();
        Assertions.assertThrows(IllegalStateException.class, () -> clientOuter.sendMessage(payload));
    }

    @Test
    void receiveMessage() throws Exception {
        CompletableFuture<Payload> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withGlobalListener((c, p) -> receiveFuture.complete(p.payload()))
                .buildAndConnect();
        Version data = getVersionPayload();
        server.send(serializeMessage(data));
        Assertions.assertEquals(data, receiveFuture.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void receiveMessageCustomConfig() throws Exception {
        CompletableFuture<Payload> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withGlobalListener((c, p) -> receiveFuture.complete(p.payload()))
                .withNetworkConfiguration(new NetworkConfiguration() {
                    @Override
                    public int getMagicValue() {
                        return 0xabababab;
                    }

                    @Override
                    public int getPort() {
                        return 8333;
                    }
                })
                .buildAndConnect();
        Version data = getVersionPayload();
        byte[] bytes = serializeMessage(data);
        bytes[0] = bytes[1] = bytes[2] = bytes[3] = (byte) 0xab;
        server.send(bytes);
        Assertions.assertEquals(data, receiveFuture.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void specificListener() throws Exception {
        CompletableFuture<Command> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withListener(Version.class, (c, p) -> receiveFuture.complete(p.payload().getCommand()))
                .buildAndConnect();
        Version data = getVersionPayload();
        server.send(serializeMessage(data));
        Assertions.assertEquals(Command.VERSION, receiveFuture.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void withMaxTimePerMessage() throws Exception {
        CompletableFuture<Command> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withListener(Version.class, (c, p) -> receiveFuture.complete(p.payload().getCommand()))
                .withMaxTimePerMessage(1)
                .buildAndConnect();
        Version data = getVersionPayload();
        byte[] msgBytes = serializeMessage(data);
        byte[] incomplete = Arrays.copyOf(msgBytes, msgBytes.length - 1);
        server.send(incomplete);
        Assertions.assertThrows(TimeoutException.class, () -> receiveFuture.get(400, TimeUnit.MILLISECONDS));
        server.send(msgBytes);
        Assertions.assertEquals(Command.VERSION, receiveFuture.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void withTrashBefore() throws Exception {
        CompletableFuture<Command> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withListener(Version.class, (c, p) -> receiveFuture.complete(p.payload().getCommand()))
                .buildAndConnect();

        byte[] trash = new byte[8192];
        new Random().nextBytes(trash);
        server.send(trash);

        Version data = getVersionPayload();
        server.send(serializeMessage(data));
        Assertions.assertEquals(Command.VERSION, receiveFuture.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void invalidMsgLength() throws Exception {
        CompletableFuture<Command> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withListener(Version.class, (c, p) -> receiveFuture.complete(p.payload().getCommand()))
                .buildAndConnect();
        Version data = getVersionPayload();
        byte[] bytes = serializeMessage(data);
        bytes[19] = 0x01;
        server.send(bytes);
        Assertions.assertThrows(TimeoutException.class, () -> receiveFuture.get(400, TimeUnit.MILLISECONDS));
    }

    @Test
    void invalidMsgChecksum() throws Exception {
        CompletableFuture<Command> receiveFuture = new CompletableFuture<>();
        BitcomClient client = BitcomClient.builder()
                .withListener(Version.class, (c, p) -> receiveFuture.complete(p.payload().getCommand()))
                .buildAndConnect();
        Version data = getVersionPayload();
        byte[] bytes = serializeMessage(data);
        bytes[23] = 0x01;
        server.send(bytes);
        Assertions.assertThrows(TimeoutException.class, () -> receiveFuture.get(400, TimeUnit.MILLISECONDS));
    }

    private <T extends Payload> byte[] serializeMessage(T payload) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageSender sender = new MessageSender(bout, StandardConfiguration.MAIN);
        sender.sendMessage(payload).get();
        return bout.toByteArray();
    }

    private Version getVersionPayload() {
        return new Version(70015, 1024, System.currentTimeMillis() / 1000,
                new NetworkAddress(0, 0x00, localAddress, 8333),
                new NetworkAddress(0, 0x00, localAddress, 8333),
                0x0, "", 1, false);
    }
}