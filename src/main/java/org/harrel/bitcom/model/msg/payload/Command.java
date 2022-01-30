package org.harrel.bitcom.model.msg.payload;

import java.util.HashMap;
import java.util.Map;

public enum Command {
    version(Version.class),
    //verack(Version.class),
//    pong(Version.class),
    ping(Ping.class);

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
        return classMap.get(clazz);
    }
}
