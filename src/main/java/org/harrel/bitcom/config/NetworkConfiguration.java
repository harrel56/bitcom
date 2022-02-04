package org.harrel.bitcom.config;

public interface NetworkConfiguration {
    int getMagicValue();
    int getPort();
    default String[] getDnsSeeders() {
        return new String[0];
    }
}
