package org.harrel.bitcom.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class BitcomServer {

    final ServerSocket server;
    Socket socket;

    public BitcomServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        CompletableFuture.supplyAsync(() -> {
            try {
                return server.accept();
            } catch (IOException e) {
                return null;
            }
        }).thenAccept((s) -> {
            socket = s;
        });
    }

    public void close() throws IOException {
        socket.close();
        server.close();
    }

    public void send(byte[] data) throws IOException {
        socket.getOutputStream().write(data);
    }


}
