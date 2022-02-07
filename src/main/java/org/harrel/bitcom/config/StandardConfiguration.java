package org.harrel.bitcom.config;

public enum StandardConfiguration implements NetworkConfiguration {
    MAIN(0xD9B4BEF9, 8333, new String[]{
            "seed.bitcoin.sipa.be",         // Pieter Wuille
            "dnsseed.bluematt.me",          // Matt Corallo
            "dnsseed.bitcoin.dashjr.org",   // Luke Dashjr
            "seed.bitcoinstats.com",        // Chris Decker
            "seed.bitcoin.jonasschnelli.ch",// Jonas Schnelli
            "seed.btc.petertodd.org",       // Peter Todd
            "seed.bitcoin.sprovoost.nl",    // Sjors Provoost
            "dnsseed.emzy.de",              // Stephan Oeste
    }),
    TESTNET3(0x0709110B, 18333, new String[]{
            "testnet-seed.bitcoin.jonasschnelli.ch", // Jonas Schnelli
            "seed.tbtc.petertodd.org",               // Peter Todd
            "seed.testnet.bitcoin.sprovoost.nl",     // Sjors Provoost
            "testnet-seed.bluematt.me",              // Matt Corallo
    });

    private final int magicValue;
    private final int port;
    private final String[] dnsSeeders;

    StandardConfiguration(int magicValue, int port, String[] dnsSeeders) {
        this.magicValue = magicValue;
        this.port = port;
        this.dnsSeeders = dnsSeeders;
    }

    @Override
    public int getMagicValue() {
        return magicValue;
    }

    @Override
    public int getPort() {
        return port;
    }

    public String[] getDnsSeeders() {
        return dnsSeeders;
    }
}
