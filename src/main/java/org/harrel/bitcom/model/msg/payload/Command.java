package org.harrel.bitcom.model.msg.payload;

public enum Command {
    version(Version.class),
    //verack(Version.class),
//    pong(Version.class),
    ping(Ping.class);

    private final Class<? extends Payload> payloadClass;

    Command(Class<? extends Payload> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public Class<? extends Payload> getPayloadClass() {
        return payloadClass;
    }
}
