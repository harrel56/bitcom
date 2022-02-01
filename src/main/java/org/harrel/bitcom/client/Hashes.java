package org.harrel.bitcom.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashes {

    private Hashes() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Hashes.class);

    public static int getPayloadChecksum(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] payloadHash = digest.digest(digest.digest(payload));
            return ByteBuffer.wrap(payloadHash).getInt();
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 not present", e);
            return 0;
        }
    }
}
