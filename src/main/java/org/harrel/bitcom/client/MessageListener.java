package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;

@FunctionalInterface
public interface MessageListener<T extends Payload> {
    void onMessageReceived(NetworkClient target, Message<T> msg);
}
