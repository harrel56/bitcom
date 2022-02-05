package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Payload;
import org.harrel.bitcom.serial.Serializer;

public abstract class PayloadSerializer<T extends Payload> extends Serializer<T> {
}
