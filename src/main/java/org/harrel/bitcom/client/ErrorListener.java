package org.harrel.bitcom.client;

@FunctionalInterface
public interface ErrorListener<T extends Exception> {
    void onError(NetworkClient target, T e);
}
