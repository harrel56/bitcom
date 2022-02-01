package org.harrel.bitcom.model.msg.payload;

public record Pong(long nonce) implements Payload {
    @Override
    public Command getCommand() {
        return Command.pong;
    }
}
