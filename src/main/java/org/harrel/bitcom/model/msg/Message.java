package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.Payload;

public record Message<T extends Payload>(Header header, T payload) {}
