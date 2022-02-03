package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;

import java.util.concurrent.CompletableFuture;

public interface NetworkClient extends AutoCloseable {
    <T extends Payload> CompletableFuture<Message<T>> sendMessage(T payload);
}
