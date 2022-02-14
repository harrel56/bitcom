package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;

public class MessageIntegrityException extends Exception {

    private final transient Message<Payload> malformedMessage;

    public MessageIntegrityException(Message<Payload> malformedMessage, String message) {
        super(message);
        this.malformedMessage = malformedMessage;
    }

    public Message<Payload> getMalformedMessage() {
        return malformedMessage;
    }
}
