package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.Command;

public record Header(int magicValue,
                     Command command,
                     int length,
                     int checksum) {

    public Header {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }
    }
}
