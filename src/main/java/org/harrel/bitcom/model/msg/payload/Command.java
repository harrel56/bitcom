package org.harrel.bitcom.model.msg.payload;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum Command {
    addr(Addr.class),
    inv(Inv.class),
    version(Version.class),
    verack(Verack.class),
    ping(Ping.class),
    pong(Pong.class);

    private static final Map<Class<?>, Command> classMap = new HashMap<>();

    static {
        for (Command value : Command.values()) {
            classMap.put(value.getPayloadClass(), value);
        }
    }

    private final Class<? extends Payload> payloadClass;

    Command(Class<? extends Payload> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public Class<? extends Payload> getPayloadClass() {
        return payloadClass;
    }

    public static Command forClass(Class<? extends Payload> clazz) {
        Command cmd = classMap.get(clazz);
        Objects.requireNonNull(cmd);
        return cmd;
    }
}
