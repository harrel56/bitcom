package org.harrel.bitcom.model;

public enum Service {
    NODE_NETWORK(1),
    NODE_GETUTXO(2), // Not yet supported
    NODE_BLOOM(4), // Not yet supported
    NODE_WITNESS(8), // Not yet supported
    NODE_COMPACT_FILTERS(64), // Not yet supported
    NODE_NETWORK_LIMITED(1024);

    private final int value;

    Service(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
