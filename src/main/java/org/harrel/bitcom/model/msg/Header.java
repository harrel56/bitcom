package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.PayloadType;

public record Header(int magicValue,
                     PayloadType payloadType,
                     int length,
                     int checksum) {
}
