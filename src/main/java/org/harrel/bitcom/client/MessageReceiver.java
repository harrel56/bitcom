package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.HeaderSerializer;
import org.harrel.bitcom.serial.SerializerFactory;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final AtomicBoolean stopped = new AtomicBoolean(true);
    private final InputStream in;

    private Thread listeningThread;

    public MessageReceiver(InputStream in) {
        this.in = in;
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
        listeningThread = new Thread(this::readLoop, this + ":listening-thread");
        listeningThread.start();
    }

    private void readLoop() {
        try {
            /* Can be interrupted before entering try-catch */
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            while (!stopped.get()) {
                byte[] headerBytes = in.readNBytes(HeaderSerializer.HEADER_SIZE);
                Header header = serializerFactory.getHeaderSerializer().deserialize(new ByteArrayInputStream(headerBytes));

                PayloadSerializer<?> payloadSerializer = serializerFactory.getPayloadSerializer(header.command());
                Payload payload = payloadSerializer.deserialize(in);
                // todo check for payload length + checksum
                logger.info(payload.toString());
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
}
