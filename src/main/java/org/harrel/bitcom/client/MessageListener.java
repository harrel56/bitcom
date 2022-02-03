package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.payload.Payload;

@FunctionalInterface
public interface MessageListener<T extends Payload> {
    void onMessageReceived(NetworkClient target, T msg);
}
