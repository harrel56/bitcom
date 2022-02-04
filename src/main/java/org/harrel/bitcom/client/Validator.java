package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Header;

class Validator {

    public void assertMessageIntegrity(Header header, byte[] payload) throws MessageIntegrityException {
        if (header.length() != payload.length) {
            throw new MessageIntegrityException("Length declared in header was not equal to payload bytes length. header=%d, payload=%d"
                    .formatted(header.length(), payload.length));
        }
        if (header.checksum() != Hashes.getPayloadChecksum(payload)) {
            throw new MessageIntegrityException("Checksum declared in header was not correct. checksum=0x%s expected=0x%s"
                    .formatted(Integer.toHexString(header.checksum()), Integer.toHexString(Hashes.getPayloadChecksum(payload))));
        }
    }
}

class MessageIntegrityException extends Exception {
    public MessageIntegrityException(String message) {
        super(message);
    }
}
