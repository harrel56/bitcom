package org.harrel.bitcom.client;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;

class MainTest {

    @Test
    void test() throws IOException, InterruptedException {
        BitcomClient client = new BitcomClient("54.199.164.163");
        client.connect();
        client.sendMessage(new Version(7016, 1L, 321,
                new NetworkAddress(1, 1L, InetAddress.getByName("127.0.0.1"), 8181),
                new NetworkAddress(2, 2L, InetAddress.getByName("127.0.0.2"), 8282),
                123L, "hello", 22, true));
        Thread.sleep(60000);
    }

}
