package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Payload;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Listeners {

    private final List<MessageListener<Payload>> globalListeners = new LinkedList<>();
    private final Map<Command, List<MessageListener<? extends Payload>>> specificListeners = new EnumMap<>(Command.class);
    private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public <T extends Payload> void addListener(Class<T> payloadClass, MessageListener<T> listener) {
        specificListeners.computeIfAbsent(Command.forClass(payloadClass), k -> new LinkedList<>()).add(listener);
    }

    public void addGlobalListener(MessageListener<Payload> listener) {
        globalListeners.add(listener);
    }

    public void notify(Message<Payload> msg) {
        LoggerFactory.getLogger(getClass()).info("executing");
        pool.execute(() -> {
            Command cmd = msg.payload().getCommand();
            List<MessageListener<? extends Payload>> list = specificListeners.computeIfAbsent(cmd, k -> new LinkedList<>());
            for (MessageListener<? extends Payload> listener : list) {
                listener.onMessageReceived(genericCast(msg.payload()));
            }
            if (list.isEmpty()) {
                for (MessageListener<Payload> globalListener : globalListeners) {
                    globalListener.onMessageReceived(genericCast(msg.payload()));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T genericCast(Payload payload) {
        return (T) payload;
    }

}
