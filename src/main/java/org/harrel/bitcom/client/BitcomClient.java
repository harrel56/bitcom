package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.harrel.bitcom.serial.SerializerFactory;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BitcomClient implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final InetAddress address;
    private final NetworkConfiguration netConfig;
    private final Listeners listeners = new Listeners();

    private Socket socket;
    private MessageReceiver receiver;

    public BitcomClient(String address) throws UnknownHostException {
        this(address, StandardConfiguration.MAIN);
    }

    public BitcomClient(String address, NetworkConfiguration netConfig) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.netConfig = netConfig;
    }

    public InetAddress getAddress() {
        return address;
    }

    public NetworkConfiguration getNetworkConfiguration() {
        return netConfig;
    }

    public void connect() throws IOException {
        socket = new Socket(address, netConfig.getPort());
        receiver = new MessageReceiver(socket.getInputStream(), listeners);
        receiver.startListening();
        logger.info("Established socket connection to {}:{}", address.getHostAddress(), netConfig.getPort());
    }

    @Override
    public void close() throws IOException {
        receiver.stop();
        socket.close();
    }

    public <T extends Payload> void addListener(Class<T> payloadClass, MessageListener<T> listener) {
        listeners.addListener(payloadClass, listener);
    }

    public void addGlobalListener(MessageListener<Payload> listener) {
        listeners.addGlobalListener(listener);
    }

    public <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload) {
        logger.info("Sending new message of type={}", payload.getCommand());
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendMessageInternal(payload);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    private synchronized <T extends Payload> Message<T> sendMessageInternal(T payload) throws IOException {
        PayloadSerializer<T> payloadSerializer = serializerFactory.getPayloadSerializer(payload);
        ByteArrayOutputStream payloadOut = new ByteArrayOutputStream(payloadSerializer.getExpectedByteSize());
        payloadSerializer.serialize(payload, payloadOut);
        byte[] payloadBytes = payloadOut.toByteArray();

        ByteArrayOutputStream out = new ByteArrayOutputStream(HeaderSerializer.HEADER_SIZE + payloadBytes.length);
        Header header = createHeader(payload, payloadBytes);
        serializerFactory.getHeaderSerializer().serialize(header, out);

        out.write(payloadBytes);
        socket.getOutputStream().write(out.toByteArray());
        logger.info("Sent message of type={}", payload.getCommand());
        return new Message<>(header, payload);
    }

    private Header createHeader(Payload payload, byte[] payloadBytes) {
        int checksum = Hashes.getPayloadChecksum(payloadBytes);
        return new Header(netConfig.getMagicValue(), payload.getCommand(), payloadBytes.length, checksum);
    }
}
