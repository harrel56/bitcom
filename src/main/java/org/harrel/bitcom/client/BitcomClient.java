package org.harrel.bitcom.client;

import org.harrel.bitcom.config.NetworkConfiguration;
import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.jmx.BitcomInfo;
import org.harrel.bitcom.jmx.JmxSupport;
import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class BitcomClient implements NetworkClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final InetAddress address;
    private final NetworkConfiguration netConfig;
    private final BitcomInfo statMBean;

    private final Socket socket;
    private final MessageSender sender;
    private final MessageReceiver receiver;

    public static Builder builder() {
        return new Builder();
    }

    private BitcomClient(InetAddress address, NetworkConfiguration netConfig,
                         Listeners.Builder listenersBuilder, int messageTimeout,
                         boolean jmxEnabled) throws IOException {
        this.address = address;
        this.netConfig = netConfig;
        this.statMBean = jmxEnabled ? JmxSupport.registeredMBean(this) : JmxSupport.detachedMBean();

        listenersBuilder.withTarget(this);
        this.socket = new Socket(address, netConfig.getPort());
        socket.setSoTimeout(messageTimeout);
        this.sender = new MessageSender(socket.getOutputStream(), netConfig, statMBean);
        this.receiver = new MessageReceiver(socket.getInputStream(), netConfig, listenersBuilder.build(), statMBean);
        logger.info("Established socket connection to {}:{}", address.getHostAddress(), netConfig.getPort());
    }

    @Override
    public <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload) {
        if (socket.isClosed()) {
            throw new IllegalStateException("Connection is closed");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload was null");
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
        statMBean.setStopDate(new Date());
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
        private int messageTimeout;
        private boolean jmx;

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

        public <T extends Payload> Builder withMessageListener(Class<T> payloadClass, MessageListener<T> listener) {
            listenersBuilder.withMessageListener(payloadClass, listener);
            return this;
        }

        public Builder withGlobalMessageListener(MessageListener<Payload> listener) {
            listenersBuilder.withGlobalMessageListener(listener);
            return this;
        }

        public Builder withMessageTimeoutListener(ErrorListener<SocketTimeoutException> listener) {
            listenersBuilder.withMessageTimeoutListener(listener);
            return this;
        }

        public Builder withMessageMalformedListener(ErrorListener<MessageIntegrityException> listener) {
            listenersBuilder.withMessageMalformedListener(listener);
            return this;
        }

        public Builder withGlobalErrorListener(ErrorListener<Exception> listener) {
            listenersBuilder.withGlobalErrorListener(listener);
            return this;
        }

        public Builder withMessageTimeout(int messageTimeout) {
            this.messageTimeout = messageTimeout;
            return this;
        }

        public Builder withJmxEnabled(boolean jmx) {
            this.jmx = jmx;
            return this;
        }

        public BitcomClient buildAndConnect() throws IOException {
            if (address == null) {
                address = InetAddress.getLocalHost();
            }
            return new BitcomClient(address, netConfig, listenersBuilder, messageTimeout, jmx);
        }
    }
}
