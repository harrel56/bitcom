package org.harrel.bitcom.model.msg.payload;

import java.util.HashMap;
import java.util.Map;

//TODO optional data field of unknown length should be handled
public record Reject(Command command, Type type, String reason) implements Payload {

    public Reject {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Reason cannot be null");
        }
    }

    public enum Type {
        MALFORMED(0x01),
        INVALID(0x10),
        OBSOLETE(0x11),
        DUPLICATE(0x12),
        NONSTANDARD(0x40),
        DUST(0x41),
        INSUFFICIENTFEE(0x42),
        CHECKPOINT(0x43);

        private static final Map<Integer, Type> valueMap = new HashMap<>();

        static {
            for (Type type : Type.values()) {
                valueMap.put(type.getValue(), type);
            }
        }

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type forValue(int val) {
            return valueMap.get(val);
        }
    }
}
