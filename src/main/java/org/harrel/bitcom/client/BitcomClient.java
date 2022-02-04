package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BitcomClient implements NetworkClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final InetAddress address;
    private final NetworkConfiguration netConfig;

    private final Socket socket;
    private final MessageSender sender;
    private final MessageReceiver receiver;

    public static Builder builder() {
        return new Builder();
    }

    private BitcomClient(InetAddress address, NetworkConfiguration netConfig,
                         Listeners.Builder listenersBuilder, int maxTimePerMessage) throws IOException {
        this.address = address;
        this.netConfig = netConfig;

        listenersBuilder.withTarget(new WrappedClient(this));
        this.socket = new Socket(address, netConfig.getPort());
        socket.setSoTimeout(maxTimePerMessage);
        this.sender = new MessageSender(socket.getOutputStream(), netConfig);
        this.receiver = new MessageReceiver(socket.getInputStream(), netConfig, listenersBuilder.build());
        logger.info("Established socket connection to {}:{}", address.getHostAddress(), netConfig.getPort());
    }

    @Override
    public <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload) {
        if (socket.isClosed()) {
            throw new IllegalStateException("Connection is closed");
        }
        return sender.sendMessage(payload);
    }

    @Override
    public synchronized void close() throws IOException {
        if (!socket.isClosed()) {
            logger.info("Closed socket connection to {}:{}", address.getHostAddress(), netConfig.getPort());
        }
        receiver.close();
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public InetAddress getAddress() {
        return address;
    }

    public NetworkConfiguration getNetworkConfiguration() {
        return netConfig;
    }

    public static class Builder {

        private InetAddress address;
        private NetworkConfiguration netConfig = StandardConfiguration.MAIN;
        private final Listeners.Builder listenersBuilder = Listeners.builder();
        private int maxTimePerMessage;

        public Builder withAddress(String address) throws UnknownHostException {
            return withAddress(InetAddress.getByName(address));
        }

        public Builder withAddress(InetAddress address) {
            this.address = address;
            return this;
        }

        public Builder withNetworkConfiguration(NetworkConfiguration netConfig) {
            this.netConfig = netConfig;
            return this;
        }

        public <T extends Payload> Builder withListener(Class<T> payloadClass, MessageListener<T> listener) {
            listenersBuilder.withListener(payloadClass, listener);
            return this;
        }

        public Builder withGlobalListener(MessageListener<Payload> listener) {
            listenersBuilder.withGlobalListener(listener);
            return this;
        }

        public Builder withMaxTimePerMessage(int maxTimePerMessage) {
            this.maxTimePerMessage = maxTimePerMessage;
            return this;
        }

        public BitcomClient buildAndConnect() throws IOException {
            if (address == null) {
                address = InetAddress.getLocalHost();
            }
            return new BitcomClient(address, netConfig, listenersBuilder, maxTimePerMessage);
        }
    }

    static class WrappedClient implements NetworkClient {
        private final NetworkClient delegate;

        public WrappedClient(NetworkClient delegate) {
            Objects.requireNonNull(delegate);
            this.delegate = delegate;
        }

        @Override
        public <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload) {
            return delegate.sendMessage(payload);
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("Closing client in listener code is not supported");
        }
    }
}
