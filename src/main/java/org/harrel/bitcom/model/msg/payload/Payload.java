package org.harrel.bitcom.model.msg.payload;

public interface Payload {
    default Command getCommand() {
        return Command.forClass(getClass());
    }
}
