package org.harrel.bitcom.model.msg.payload;

public record Verack() implements Payload {

    @Override
    public Command getCommand() {
        return Command.verack;
    }
}
