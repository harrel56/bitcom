package org.harrel.bitcom.client;

import org.harrel.bitcom.config.StandardConfiguration;
import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Ping;
import org.harrel.bitcom.model.msg.payload.Verack;
import org.harrel.bitcom.model.msg.payload.Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

class MainTest {

    @Disabled
    @Test
    void test() throws IOException, InterruptedException {
        BitcomClient client = BitcomClient.builder().withAddress("81.169.184.84")
                .withNetworkConfiguration(StandardConfiguration.MAIN)
                .withMessageListener(Version.class, (c, p) -> {
                    c.sendMessage(new Verack());
                    c.sendMessage(new Ping(99999));
                })
                .withGlobalMessageListener((c, p) -> System.out.println(p))
                .withJmxEnabled(true)
                .buildAndConnect();

        client.sendMessage(new Version(70015, Set.of(), 321,
                new NetworkAddress(1, Set.of(), InetAddress.getByName("127.0.0.1"), 8181),
                new NetworkAddress(2, Set.of(), InetAddress.getByName("127.0.0.2"), 8282),
                123L, "hello", 22, true));
        Thread.sleep(6000000);
    }

}
