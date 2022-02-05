package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.io.TeeInputStream;
import org.harrel.bitcom.model.msg.Header;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.SerializerFactory;
import org.harrel.bitcom.serial.payload.PayloadSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.harrel.bitcom.serial.HeaderSerializer.HEADER_SIZE;
import static org.harrel.bitcom.serial.HeaderSerializer.MAGIC_SIZE;

class MessageReceiver implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SerializerFactory serializerFactory = new SerializerFactory();
    private final Validator validator = new Validator();
    private final InputStream in;
    private final Listeners listeners;

    private final Thread listeningThread;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final List<byte[]> magicValues;

    MessageReceiver(InputStream in, NetworkConfiguration netConfig, Listeners listeners) {
        this.in = in;
        this.listeners = listeners;
        this.magicValues = initMagicValues(netConfig);
        this.listeningThread = new Thread(this::readLoop, getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ":listening-thread");
        this.listeningThread.start();
    }

    @Override
    public synchronized void close() {
        listeningThread.interrupt();
    }

    private List<byte[]> initMagicValues(NetworkConfiguration netConfig) {
        Set<Integer> magics = new HashSet<>();
        magics.add(netConfig.getMagicValue());
        for (StandardConfiguration config : StandardConfiguration.values()) {
            magics.add(config.getMagicValue());
        }
        return magics.stream()
                .map(mv -> serializerFactory.getHeaderSerializer().serializeMagicValueAsBytes(mv))
                .toList();
    }

    private void readLoop() {
        try (in) {
            while (!Thread.interrupted()) {
                readMessage();
            }
        } catch (InterruptedIOException | InterruptedException e) {
            logger.debug("Listening thread interrupted. Stopping...");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("Unrecoverable exception occurred in listening thread. Stopping socket listener...", e);
        }
    }

    private void readMessage() throws IOException, InterruptedException {
        try {
            Header header = readHeader(in);

            PayloadSerializer<?> payloadSerializer = serializerFactory.getPayloadSerializer(header.command());
            ByteArrayOutputStream teeOutput = new ByteArrayOutputStream();
            Payload payload = payloadSerializer.deserialize(new TeeInputStream(in, teeOutput));

            validator.assertMessageIntegrity(header, teeOutput.toByteArray());
            listeners.notify(new Message<>(header, payload));

        } catch (SocketTimeoutException e) {
            logger.warn("Reading message timed out. Skipping");
        } catch (MessageIntegrityException e) {
            logger.warn("Ignoring malformed message. {}", e.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Message deserialization failed", e);
        }
    }

    private Header readHeader(InputStream in) throws IOException, InterruptedException {
        byte[] magicBytes = readMagicValue(in);
        byte[] headerBytes = Arrays.copyOfRange(magicBytes, 0, HEADER_SIZE);
        in.readNBytes(headerBytes, MAGIC_SIZE, HEADER_SIZE - MAGIC_SIZE);
        return serializerFactory.getHeaderSerializer().deserialize(new ByteArrayInputStream(headerBytes));
    }

    private byte[] readMagicValue(InputStream in) throws IOException, InterruptedException {
        logger.debug("Waiting for magic value...");
        byte[] tmp = new byte[MAGIC_SIZE];
        buffer.clear();
        while (true) {
            while (in.available() > 0) {
                buffer.put((byte) in.read());
                if (buffer.position() >= MAGIC_SIZE) {
                    buffer.get(buffer.position() - MAGIC_SIZE, tmp);
                    if (isMagicValue(tmp)) {
                        logger.trace("magicValue={}", Arrays.toString(tmp));
                        return tmp;
                    }
                }
                if (buffer.capacity() == buffer.position()) {
                    logger.trace("Rewinding magic value buffer. availableBytes={}", in.available());
                    buffer.get(buffer.capacity() - MAGIC_SIZE, tmp);
                    buffer.rewind();
                    buffer.put(tmp);
                }
            }

            Thread.sleep(200);
        }
    }

    private boolean isMagicValue(byte[] data) {
        for (byte[] magic : magicValues) {
            if (Arrays.mismatch(data, magic) == -1) {
                return true;
            }
        }
        return false;
    }
}
