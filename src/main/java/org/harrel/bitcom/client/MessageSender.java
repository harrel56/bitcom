package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
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
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class MessageSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final OutputStream out;
    private final NetworkConfiguration netConfig;

    MessageSender(OutputStream out, NetworkConfiguration netConfig) {
        this.out = out;
        this.netConfig = netConfig;
    }

    <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload) {
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
        ByteArrayOutputStream payloadOut = new ByteArrayOutputStream();
        payloadSerializer.serialize(payload, payloadOut);
        byte[] payloadBytes = payloadOut.toByteArray();

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(HeaderSerializer.HEADER_SIZE + payloadBytes.length);
        Header header = createHeader(payload, payloadBytes);
        serializerFactory.getHeaderSerializer().serialize(header, byteOut);

        byteOut.write(payloadBytes);
        out.write(byteOut.toByteArray());
        logger.info("Sent message of type={}", payload.getCommand());
        return new Message<>(header, payload);
    }

    private Header createHeader(Payload payload, byte[] payloadBytes) {
        int checksum = Hashes.getPayloadChecksum(payloadBytes);
        return new Header(netConfig.getMagicValue(), payload.getCommand(), payloadBytes.length, checksum);
    }
}
