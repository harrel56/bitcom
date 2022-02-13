package org.harrel.bitcom.model.msg.payload;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum Command {
    VERSION(Version.class),
    VERACK(Verack.class),
    ADDR(Addr.class),
    INV(Inv.class),
    GETDATA(GetData.class),
    NOTFOUND(NotFound.class),
    GETBLOCKS(GetBlocks.class),
    GETHEADERS(GetHeaders.class),
    TX(Tx.class),
    BLOCK(Block.class),
    HEADERS(Headers.class),
    GETADDR(GetAddr.class),
    MEMPOOL(MemPool.class),
    PING(Ping.class),
    PONG(Pong.class),
    REJECT(Reject.class);

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
