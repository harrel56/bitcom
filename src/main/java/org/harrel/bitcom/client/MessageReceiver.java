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
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final Validator validator = new Validator();
    private final AtomicBoolean stopped = new AtomicBoolean(true);
    private final InputStream in;
    private final Listeners listeners;

    private Thread listeningThread;

    public MessageReceiver(InputStream in, Listeners listeners) {
        this.in = in;
        this.listeners = listeners;
    }

    public void stop() {
        if (!stopped.compareAndSet(false, true)) {
            throw new IllegalStateException("Already stopped");
        }
        listeningThread.interrupt();
    }

    public void startListening() {
        if (!stopped.compareAndSet(true, false)) {
            throw new IllegalStateException("Already listening");
        }
        listeningThread = new Thread(this::readLoop, getClass().getSimpleName() + "@" + hashCode() + ":listening-thread");
        listeningThread.start();
    }

    private void readLoop() {
        try {
            /* Can be interrupted before entering try-catch */
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            while (!stopped.get()) {
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
            logger.info("Listening thread interrupted. Stopping...");
            Thread.currentThread().interrupt();
            try {
                in.close();
            } catch (IOException ioe) { /* close silently */}
        } catch (IOException e) {
            logger.error("Exception occurred in listening thread", e);
        } catch (InterruptedException e) {
            logger.info("Listening thread interrupted. Stopping...");
            Thread.currentThread().interrupt();
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
