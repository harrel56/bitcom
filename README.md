# Bitcom [![pipeline status](https://gitlab.com/org.harrel/bitcom/badges/master/pipeline.svg)](https://gitlab.com/org.harrel/bitcom/-/commits/master) [![coverage report](https://gitlab.com/org.harrel/bitcom/badges/master/coverage.svg)](https://gitlab.com/org.harrel/bitcom/-/commits/master)
***

Java library for bitcoin P2P network communication. Implemented with plain java.io sockets and based on message listeners. Implementation based on specification from [this protocol documentation](https://en.bitcoin.it/wiki/Protocol_documentation).

## Release 0.5 - latest
* Core functionalities - making connection, sending messages and receiving,
* basic message model, **not containing** any of the BIPs
* configurable message timeout,
* error listeners,
* JMX support with 1 basic MBean

## Usage
Connect to specific host:
```java
BitcomClient client = BitcomClient.builder()
    .withAddress("example.com") // the only required value
    .buildAndConnect();
```
Socket connection will be maintained until explicitly closed:
```java
client.close();
```
Sending message is non-blocking, upon successful send, returned future will be resolved (e.g. message header can be accessed then):
```java
CompletableFuture<Message<Ping>> future = client.sendMessage(new Ping(nonce));
Header header = future.get().header();
```
Available options on client builder:
```java
BitcomClient client = BitcomClient.builder()
    .withAddress("example.com")
     // standard network configs are found in org.harrel.bitcom.config.StandardConfiguration
    .withNetworkConfiguration(new NetworkConfiguration() {
        @Override
        public int getMagicValue() {
            return 0x0709110B;
        }

        @Override
        public int getPort() {
            return 11111;
        }
    })
     // called when Version message is received
    .withMessageListener(Version.class, (client, msg) -> {
        client.sendMessage(new Verack());
        client.sendMessage(new Ping(99999));
    })
    // listener called on any message received
    .withGlobalMessageListener((client, msg) -> System.out.println(p))
     // maximum time to wait (in ms) for each message before being discarded - default is 5000 (5s)
    .withMessageTimeout(2000)
     // listener called when receiving message has timed out
    .withMessageTimeoutListener((client, exception) -> System.err.println(e))
     // listener called when message is inconsistent with header metadata (length, checksum)
    .withMessageMalformedListener((client, exception) -> System.err.println(e))
     // listener called for any error mentioned above and for any other unexpected errors
    .withGlobalErrorListener((client, exception) -> System.err.println(e))
    .withJmxEnabled(true)
    .buildAndConnect();
```