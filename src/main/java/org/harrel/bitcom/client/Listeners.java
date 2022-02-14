package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class Listeners {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NetworkClient target;
    private final List<MessageListener<Payload>> globalListeners;
    private final Map<Command, List<MessageListener<? extends Payload>>> specificListeners;
    private final List<ErrorListener<Exception>> globalErrorListeners;
    private final List<ErrorListener<SocketTimeoutException>> msgTimeoutListeners;
    private final List<ErrorListener<MessageIntegrityException>> msgMalformedListeners;
    private final ThreadPoolExecutor pool;

    static Builder builder() {
        return new Builder();
    }

    private Listeners(NetworkClient target,
                      List<MessageListener<Payload>> globalListeners,
                      Map<Command, List<MessageListener<? extends Payload>>> specificListeners,
                      List<ErrorListener<Exception>> globalErrorListeners,
                      List<ErrorListener<SocketTimeoutException>> msgTimeoutListeners,
                      List<ErrorListener<MessageIntegrityException>> msgMalformedListeners) {
        this.target = target;
        this.globalListeners = globalListeners;
        this.specificListeners = specificListeners;
        this.globalErrorListeners = globalErrorListeners;
        this.msgTimeoutListeners = msgTimeoutListeners;
        this.msgMalformedListeners = msgMalformedListeners;
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    void notify(Message<Payload> msg) {
        pool.execute(() -> {
            logger.trace("Listeners notified with msg={}", msg);
            Command cmd = msg.payload().getCommand();
            List<MessageListener<? extends Payload>> list = specificListeners.getOrDefault(cmd, List.of());

            list.forEach(l -> l.onMessageReceived(target, msgCast(msg)));
            globalListeners.forEach(l -> l.onMessageReceived(target, msgCast(msg)));
        });
    }

    void notifyError(Exception e) {
        pool.execute(() -> globalErrorListeners.forEach(l -> l.onError(target, e)));
    }

    void notifyError(SocketTimeoutException e) {
        pool.execute(() -> {
            msgTimeoutListeners.forEach(l -> l.onError(target, e));
            globalErrorListeners.forEach(l -> l.onError(target, e));
        });
    }

    void notifyError(MessageIntegrityException e) {
        pool.execute(() -> {
            msgMalformedListeners.forEach(l -> l.onError(target, e));
            globalErrorListeners.forEach(l -> l.onError(target, e));
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends Payload> Message<T> msgCast(Message<Payload> msg) {
        return (Message<T>) msg;
    }

    static class Builder {
        private NetworkClient target;
        private final List<MessageListener<Payload>> globalListeners = new ArrayList<>();
        private final Map<Command, List<MessageListener<? extends Payload>>> specificListeners = new EnumMap<>(Command.class);
        private final List<ErrorListener<Exception>> globalErrorListeners = new ArrayList<>();
        private final List<ErrorListener<SocketTimeoutException>> msgTimeoutListeners = new ArrayList<>();
        private final List<ErrorListener<MessageIntegrityException>> msgMalformedListeners = new ArrayList<>();

        private Builder() {
        }

        Builder withGlobalMessageListener(MessageListener<Payload> listener) {
            globalListeners.add(listener);
            return this;
        }

        <T extends Payload> Builder withMessageListener(Class<T> payloadClass, MessageListener<T> listener) {
            specificListeners.computeIfAbsent(Command.forClass(payloadClass), k -> new LinkedList<>()).add(listener);
            return this;
        }

        Builder withGlobalErrorListener(ErrorListener<Exception> listener) {
            globalErrorListeners.add(listener);
            return this;
        }

        Builder withMessageTimeoutListener(ErrorListener<SocketTimeoutException> listener) {
            msgTimeoutListeners.add(listener);
            return this;
        }

        Builder withMessageMalformedListener(ErrorListener<MessageIntegrityException> listener) {
            msgMalformedListeners.add(listener);
            return this;
        }

        Builder withTarget(NetworkClient target) {
            this.target = target;
            return this;
        }

        Listeners build() {
            var global = Collections.unmodifiableList(globalListeners);
            specificListeners.replaceAll((cmd, li) -> Collections.unmodifiableList(li));
            var specific = Collections.unmodifiableMap(specificListeners);
            var globalError = Collections.unmodifiableList(globalErrorListeners);
            var msgTimeout = Collections.unmodifiableList(msgTimeoutListeners);
            var msgMalformed = Collections.unmodifiableList(msgMalformedListeners);
            return new Listeners(target, global, specific, globalError, msgTimeout, msgMalformed);
        }
    }

}
