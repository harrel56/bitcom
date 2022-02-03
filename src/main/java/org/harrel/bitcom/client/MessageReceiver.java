package org.harrel.bitcom.client;

import org.harrel.bitcom.io.TeeInputStream;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.harrel.bitcom.serial.SerializerFactory;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

class MessageReceiver implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final Validator validator = new Validator();
    private final InputStream in;
    private final Listeners listeners;

    private final Thread listeningThread;

    MessageReceiver(InputStream in, Listeners listeners) {
        this.in = in;
        this.listeners = listeners;
        this.listeningThread = new Thread(this::readLoop, getClass().getSimpleName() + "@" + hashCode() + ":listening-thread");
        this.listeningThread.start();
    }

    @Override
    public synchronized void close() {
        listeningThread.interrupt();
    }

    private void readLoop() {
        try {
            while (!Thread.interrupted()) {
                Header header = readHeader(in);
                if (header == null) {
                    continue;
                }

                PayloadSerializer<?> payloadSerializer = serializerFactory.getPayloadSerializer(header.command());
                ByteArrayOutputStream teeOutput = new ByteArrayOutputStream(payloadSerializer.getExpectedByteSize());
                Payload payload = payloadSerializer.deserialize(new TeeInputStream(in, teeOutput));

                List<String> errors = validator.validateMessageIntegrity(header, teeOutput.toByteArray());
                if (errors.isEmpty()) {
                    listeners.notify(new Message<>(header, payload));
                } else {
                    logger.warn("Ignoring malformed message. Reason:");
                    errors.forEach(logger::warn);
                }
            }
        } catch (InterruptedIOException e) {
            logger.debug("Listening thread interrupted. Stopping...");
            Thread.currentThread().interrupt();
            try {
                in.close();
            } catch (IOException ioe) { /* close silently */}
        } catch (IOException e) {
            logger.error("Exception occurred in listening thread", e);
        }
    }

    private Header readHeader(InputStream in) throws IOException {
        logger.debug("Waiting for header bytes...");
        try {
            byte[] headerBytes = in.readNBytes(HeaderSerializer.HEADER_SIZE);
            return serializerFactory.getHeaderSerializer().deserialize(new ByteArrayInputStream(headerBytes));
        } catch (RuntimeException e) {
            logger.warn("Header deserialization failed", e);
            return null;
        }
    }
}
