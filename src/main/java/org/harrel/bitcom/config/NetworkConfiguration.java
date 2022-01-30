package org.harrel.bitcom.config;

public interface NetworkConfiguration {
    int getMagicValue();
    int getPort();
    String[] getDnsSeeders();
}
