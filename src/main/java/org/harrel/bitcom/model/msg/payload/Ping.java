package org.harrel.bitcom.model.msg.payload;

public record Ping(long nonce) implements Payload {
}
