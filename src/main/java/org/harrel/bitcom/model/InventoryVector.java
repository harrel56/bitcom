package org.harrel.bitcom.model;

import java.util.HashMap;
import java.util.Map;

public record InventoryVector(Type type, String hash) {

    public InventoryVector {
        if (type == null) {
            throw new IllegalArgumentException("Inventory vector type cannot be null");
        }
        if (hash == null || hash.length() != 32) {
            throw new IllegalArgumentException("Inventory vector hash must be 32 long");
        }
    }

    public enum Type {
        ERROR(0x00),
        MSG_TX(0x01),
        MSG_BLOCK(0x02),
        MSG_FILTERED_BLOCK(0x03),
        MSG_CMPCT_BLOCK(0x04),
        MSG_WITNESS_TX(0x40000001),
        MSG_WITNESS_BLOCK(0x40000002),
        MSG_FILTERED_WITNESS_BLOCK(0x40000003);

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

        public static Type forValue(int value) {
            return valueMap.get(value);
        }
    }
}
