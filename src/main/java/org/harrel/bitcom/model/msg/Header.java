package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.Command;

public record Header(int magicValue,
                     Command command,
                     int length,
                     int checksum) {
}
