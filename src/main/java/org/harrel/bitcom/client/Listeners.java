package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class Listeners {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NetworkClient target;
    private final List<MessageListener<Payload>> globalListeners;
    private final Map<Command, List<MessageListener<? extends Payload>>> specificListeners;
    private final ThreadPoolExecutor pool;

    static Builder builder() {
        return new Builder();
    }

    private Listeners(NetworkClient target, List<MessageListener<Payload>> globalListeners, Map<Command, List<MessageListener<? extends Payload>>> specificListeners) {
        this.target = target;
        this.globalListeners = globalListeners;
        this.specificListeners = specificListeners;
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    void notify(Message<Payload> msg) {
        pool.execute(() -> {
            logger.trace("Listeners notified with msg={}", msg);
            Command cmd = msg.payload().getCommand();
            List<MessageListener<? extends Payload>> list = specificListeners.getOrDefault(cmd, List.of());
            for (MessageListener<? extends Payload> listener : list) {
                listener.onMessageReceived(target, genericCast(msg.payload()));
            }
            for (MessageListener<Payload> globalListener : globalListeners) {
                globalListener.onMessageReceived(target, genericCast(msg.payload()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T genericCast(Payload payload) {
        return (T) payload;
    }

    static class Builder {
        private NetworkClient target;
        private final List<MessageListener<Payload>> globalListeners = new LinkedList<>();
        private final Map<Command, List<MessageListener<? extends Payload>>> specificListeners = new EnumMap<>(Command.class);

        private Builder() {
        }

        Builder withGlobalListener(MessageListener<Payload> listener) {
            globalListeners.add(listener);
            return this;
        }

        <T extends Payload> Builder withListener(Class<T> payloadClass, MessageListener<T> listener) {
            specificListeners.computeIfAbsent(Command.forClass(payloadClass), k -> new LinkedList<>()).add(listener);
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
            return new Listeners(target, global, specific);
        }
    }

}
